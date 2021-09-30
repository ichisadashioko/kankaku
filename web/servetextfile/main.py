import os
import argparse
import re

import bottle

parser = argparse.ArgumentParser(description='serve text file content')
parser.add_argument('--port', type=int, default=8080)
parser.add_argument('--host', default='0.0.0.0')
parser.add_argument('--path', default='.')

args = parser.parse_args()
print('args', args)

SERVING_ROOT_PATH = os.path.abspath(args.path)

if not os.path.exists(SERVING_ROOT_PATH):
    raise Exception(f'SERVING_ROOT_PATH ({SERVING_ROOT_PATH}) does not exist')

MODULE_PARENT_PATH = os.path.dirname(os.path.abspath(__file__))
ASSETS_ROOT_PATH = os.path.join(MODULE_PARENT_PATH, 'assetsroot')

if not os.path.exists(ASSETS_ROOT_PATH):
    raise Exception(f'ASSETS_ROOT_PATH: {ASSETS_ROOT_PATH} does not exist')

# TODO split path components into a list and decode escaped characters in each components


def normalize_path(path):
    return re.sub(r'[\\\/]+', '/', path)


@bottle.post('/listdirectory')
def listdirectory():
    form_data = bottle.request.forms.decode(encoding='utf-8')
    if 'filepath' in form_data:
        requested_filepath = form_data['filepath']
    else:
        requested_filepath = '/'

    absolute_filepath = f'{SERVING_ROOT_PATH}/{requested_filepath}'
    absolute_filepath = normalize_path(absolute_filepath)
    absolute_filepath = os.path.abspath(absolute_filepath)

    if not absolute_filepath.lower().startswith(SERVING_ROOT_PATH.lower()):
        # return 403
        return bottle.HTTPError(403, f'Forbidden accessing {requested_filepath}')

    if not os.path.exists(absolute_filepath):
        return bottle.HTTPError(404, f'File Not Found {requested_filepath}')

    if not os.path.isdir(absolute_filepath):
        return bottle.HTTPError(403, f'path is not a directory {requested_filepath}')

    filename_list = os.listdir(absolute_filepath)
    fileinfo_list = []
    for filename in filename_list:
        filepath = os.path.join(absolute_filepath, filename)
        if os.path.isdir(filepath):
            filetype = 'dir'
        else:
            filetype = 'file'

        fileinfo_list.append({
            'name': filename,
            'type': filetype
        })

    # return json response
    return {
        'fileinfo_list': fileinfo_list,
    }


@bottle.post('/getfilemodifiedtime')
def getfilemodifiedtime():
    form_data = bottle.request.forms.decode(encoding='utf-8')
    if 'filepath' in form_data:
        requested_filepath = form_data['filepath']
    else:
        requested_filepath = '/'

    absolute_filepath = f'{SERVING_ROOT_PATH}/{requested_filepath}'
    absolute_filepath = normalize_path(absolute_filepath)
    absolute_filepath = os.path.abspath(absolute_filepath)

    if not absolute_filepath.lower().startswith(SERVING_ROOT_PATH.lower()):
        # return 403
        return bottle.HTTPError(403, f'Forbidden accessing {requested_filepath}')

    if not os.path.exists(absolute_filepath):
        return bottle.HTTPError(404, f'File Not Found {requested_filepath}')

    return bottle.HTTPResponse(
        status=200,
        headers={
            'Content-Type': 'text/plain',
        },
        body=str(os.path.getmtime(absolute_filepath))
    )


@bottle.post('/gettextfilecontent')
def gettextfilecontent():
    form_data = bottle.request.forms.decode(encoding='utf-8')
    if 'filepath' in form_data:
        requested_filepath = form_data['filepath']
    else:
        requested_filepath = '/'
    absolute_filepath = f'{SERVING_ROOT_PATH}/{requested_filepath}'
    absolute_filepath = normalize_path(absolute_filepath)
    absolute_filepath = os.path.abspath(absolute_filepath)

    if not absolute_filepath.lower().startswith(SERVING_ROOT_PATH.lower()):
        # return 403
        return bottle.HTTPError(403, f'Forbidden accessing {requested_filepath}')

    if not os.path.exists(absolute_filepath):
        return bottle.HTTPError(404, f'File Not Found {requested_filepath}')

    # check if the file is a text file
    with open(absolute_filepath, 'rb') as infile:
        file_content_bs = infile.read()

    try:
        file_content = file_content_bs.decode('utf-8')
    except UnicodeDecodeError:
        # return 403
        return bottle.HTTPError(403, f'Forbidden accessing {requested_filepath} - not a text file')

    # return the file content
    return bottle.HTTPResponse(file_content, content_type='text/plain')


@bottle.route('/<filepath>')
def serve_file(filepath):
    return bottle.static_file(filepath, root=ASSETS_ROOT_PATH)


@bottle.route('/')
def serve_index():
    # redirect to index.html
    return bottle.static_file('index.html', root=ASSETS_ROOT_PATH)


# start the server
bottle.run(host=args.host, port=args.port, debug=True)
