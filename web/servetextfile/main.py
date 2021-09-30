import os
import argparse

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


# @bottle.route('/listdirectory/<relativefilepath:path>')
# @bottle.route('/listdirectory:re:.+')
def listdirectory():
    current_request_url = bottle.request.url
    relativefilepath = current_request_url[len('/listdirectory'):]
    if relativefilepath in ('', '.', './', '/'):
        relativefilepath = SERVING_ROOT_PATH

    absolute_filepath = os.path.join(SERVING_ROOT_PATH, relativefilepath)
    absolute_filepath = os.path.abspath(absolute_filepath)

    if not absolute_filepath.lower().startswith(SERVING_ROOT_PATH.lower()):
        # return 403
        return bottle.HTTPError(403, f'Forbidden accessing {relativefilepath}')

    if not os.path.exists(absolute_filepath):
        return bottle.HTTPError(404, f'File Not Found {relativefilepath}')

    if not os.path.isdir(absolute_filepath):
        return bottle.HTTPError(403, f'path is not a directory {relativefilepath}')

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


# @bottle.route('/getfilemodifiedtime/<relativefilepath:path>')
def getfilemodifiedtime(relativefilepath: str):
    absolute_filepath = os.path.join(SERVING_ROOT_PATH, relativefilepath)
    absolute_filepath = os.path.abspath(absolute_filepath)

    if not absolute_filepath.lower().startswith(SERVING_ROOT_PATH.lower()):
        # return 403
        return bottle.HTTPError(403, f'Forbidden accessing {relativefilepath}')

    if not os.path.exists(absolute_filepath):
        return bottle.HTTPError(404, f'File Not Found {relativefilepath}')

    return bottle.HTTPResponse(
        status=200,
        headers={
            'Content-Type': 'text/plain',
        },
        body=str(os.path.getmtime(absolute_filepath))
    )


# @bottle.route('/gettextfilecontent/<relativefilepath:path>')
def gettextfilecontent(relativefilepath: str):
    absolute_filepath = os.path.join(SERVING_ROOT_PATH, relativefilepath)
    absolute_filepath = os.path.abspath(absolute_filepath)

    if not absolute_filepath.lower().startswith(SERVING_ROOT_PATH.lower()):
        # return 403
        return bottle.HTTPError(403, f'Forbidden accessing {relativefilepath}')

    if not os.path.exists(absolute_filepath):
        return bottle.HTTPError(404, f'File Not Found {relativefilepath}')

    # check if the file is a text file
    file_content_bs = open(absolute_filepath, 'rb').read()
    try:
        file_content = file_content_bs.decode('utf-8')
    except UnicodeDecodeError:
        # return 403
        return bottle.HTTPError(403, f'Forbidden accessing {relativefilepath} - not a text file')

    # return the file content
    return bottle.HTTPResponse(file_content, content_type='text/plain')


# @bottle.route('/<filepath>')
def serve_file(filepath):
    return bottle.static_file(filepath, root=ASSETS_ROOT_PATH)


# @bottle.route('/')
def serve_index():
    # redirect to index.html
    return bottle.static_file('index.html', root=ASSETS_ROOT_PATH)


# start the server
bottle.run(host=args.host, port=args.port, debug=True)
