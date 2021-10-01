var current_viewing_filepath = null;
var current_viewing_file_modified_time = null;
var content_loaded = false;

var current_directory_path = '';
var filetree_root_node = {};

var directorylisting_container = document.getElementById('directorylisting');
var directorynavigation_container = document.getElementById('directorynavigation');

var filecontent_container = document.getElementById('filecontent');
var filename_container = document.getElementById('filename');

function normalize_filepath(filepath) {
    return filepath.replace(/\/+/g, '/');
}

var DEFAULT_RELOAD_TIMEOUT = 1000;

/**
 *
 * @param {string} path
 * @param {Array} fileinfo_list
 */
function display_filetree(path, fileinfo_list) {
    // split path components
    path = normalize_filepath(path);
    var path_components = path.split('/');

    directorynavigation_container.innerHTML = '';

    var current_node = filetree_root_node;
    let current_node_filepath = '/';
    let navigation_element = document.createElement('div');
    navigation_element.textContent = '/';
    navigation_element.classList.add('directorynavigation-item');
    navigation_element.addEventListener('click', function (event) {
        get_directory_info(current_node_filepath);
    });

    directorynavigation_container.appendChild(navigation_element);

    for (let i = 0; i < path_components.length; i++) {
        let filename = path_components[i];
        if (filename.length === 0) {
            continue;
        }

        current_node_filepath += `${filename}/`;

        let navigation_element = document.createElement('div');
        navigation_element.textContent = `${filename}/`;
        navigation_element.classList.add('directorynavigation-item');
        navigation_element.addEventListener('click', function (event) {
            get_directory_info(current_node_filepath);
        });
        directorynavigation_container.appendChild(navigation_element);

        if (current_node[filename] == null) {
            // add a new branch
            current_node[filename] = {};
        }

        current_node = current_node[filename];
    }

    // TODO preseve some additional information in the old node
    current_node.fileinfo_list = fileinfo_list
    // set the current directory path
    current_directory_path = path;
    // display the file tree
    directorylisting_container.innerHTML = "";

    for (let i = 0; i < fileinfo_list.length; i++) {
        let fileinfo = fileinfo_list[i];
        let filename = fileinfo.name;
        let filetype = fileinfo.type;

        if (filetype == 'dir') {
            current_node[filename] = {}
            let new_node = document.createElement("li");
            new_node.textContent = filename;
            new_node.classList.add("directory");

            new_node.addEventListener("click", function (event) {
                get_directory_info(`${path}/${filename}`)
            });

            directorylisting_container.appendChild(new_node);
        } else {
            let new_node = document.createElement("li");
            new_node.textContent = filename;
            new_node.classList.add("file");
            new_node.addEventListener("click", function (event) {
                display_filecontent(`${path}/${filename}`)
            });

            directorylisting_container.appendChild(new_node);
        }
    }
}

/**
 *
 * @param {string} filepath
 * @param {number} timeout
 */
function auto_reload_filecontent(filepath, timeout) {
    if (current_viewing_file_modified_time == null) {
        setTimeout(function () {
            console.log(`skip reload current_viewing_file_modified_time == null`);
            auto_reload_filecontent(filepath, timeout);
        }, timeout);
        return;
    }

    if (!content_loaded) {
        setTimeout(function () {
            console.log(`skip reload !content_loaded`);
            auto_reload_filecontent(filepath, timeout);
        }, timeout);
        return;
    }

    if (current_viewing_filepath !== filepath) {
        console.log(`end reload current_viewing_filepath !== filepath`)
        return;
    }

    {
        let xhr = new XMLHttpRequest();
        xhr.addEventListener('load', function (event) {
            let response_str = event.target.responseText;
            let modifiedTime = parseInt(response_str);

            if (modifiedTime > current_viewing_file_modified_time) {
                current_viewing_file_modified_time = modifiedTime;
                {
                    let xhr = new XMLHttpRequest();
                    xhr.addEventListener('load', function (event) {
                        filecontent_container.innerHTML = event.target.responseText;
                        setTimeout(function () {
                            auto_reload_filecontent(filepath, timeout);
                        }, timeout);
                    });

                    let form_data = {
                        'filepath': filepath,
                    };

                    let serialized_form_data = serialize_form_fields(form_data);
                    xhr.open('POST', '/gettextfilecontent');
                    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
                    xhr.send(serialized_form_data);
                }
            } else {
                setTimeout(function () {
                    auto_reload_filecontent(filepath, timeout);
                }, timeout);
            }
        });

        let form_data = {
            'filepath': filepath,
        };

        let serialized_form_data = serialize_form_fields(form_data);
        xhr.open('POST', '/getfilemodifiedtime');
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.send(serialized_form_data);
    }
}

function display_filecontent(path) {
    path = normalize_filepath(path);
    current_viewing_filepath = path;
    var path_components = path.split('/');
    var current_node = filetree_root_node
    for (let i = 0; i < (path_components.length - 1); i++) {
        let filename = path_components[i];
        if (filename.length === 0) {
            continue;
        }

        if (current_node[filename] == null) {
            // add a new branch
            current_node[filename] = {};
        }

        current_node = current_node[filename];
    }

    // TODO preseve some additional information in the old node
    let filename = path_components[path_components.length - 1];
    filename_container.textContent = filename;
    filename_container.setAttribute('alt', path)
    current_node = current_node[filename];
    if (current_node == null) {
        current_node = {};
    }

    {
        let xhr = new XMLHttpRequest();
        xhr.addEventListener('load', function (event) {
            let response_str = event.target.responseText;
            let modifiedTime = parseInt(response_str);
            current_node.modifiedTime = modifiedTime;
            current_viewing_file_modified_time = modifiedTime;
        });

        let form_data = {
            'filepath': path,
        };

        let serialized_form_data = serialize_form_fields(form_data);
        xhr.open('POST', '/getfilemodifiedtime');
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.send(serialized_form_data);
    }

    {
        let xhr = new XMLHttpRequest();
        xhr.addEventListener('load', function (event) {
            let response_str = event.target.responseText;
            let filecontent = response_str;
            current_node.filecontent = filecontent;
            filecontent_container.textContent = filecontent;
            content_loaded = true;

            auto_reload_filecontent(path, 1000);
        });

        let form_data = {
            'filepath': path,
        };

        let serialized_form_data = serialize_form_fields(form_data);
        xhr.open('POST', '/gettextfilecontent');
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.send(serialized_form_data);
    }
}

function serialize_form_fields(form_data) {
    var serialized_string = '';
    for (var property in form_data) {
        // console.log(property);

        if (property.length === 0) {
            continue;
        } else {
            var value = form_data[property];
            if (typeof (value) !== 'string') {
                value = '' + value;
            }

            if (serialized_string.length !== 0) {
                serialized_string += '&';
            }

            serialized_string += `${encodeURIComponent(property)}=${encodeURIComponent(value)}`;
        }
    }

    return serialized_string;
}

function get_directory_info(path) {
    path = normalize_filepath(path);
    let xhr = new XMLHttpRequest();
    xhr.addEventListener("load", function (event) {
        let response_obj = JSON.parse(event.target.responseText);
        display_filetree(path, response_obj.fileinfo_list);
    });

    let form_data = {
        'filepath': normalize_filepath(path),
    };

    let encoded_form_data_str = serialize_form_fields(form_data);

    xhr.open('POST', '/listdirectory');
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.send(encoded_form_data_str);
}

function main() {
    // send a request to get the file tree
    get_directory_info("/");
}

window.onload = main;
