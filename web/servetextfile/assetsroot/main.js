var current_viewing_filepath = "";
var current_viewing_file_modified_time = 0;

var current_directory_path = "";
var filetree_root_node = {};

var directorylisting_container = document.getElementById("directorylisting");

function normalize_filepath(filepath) {
    return filepath.replace(/\/+/g, "/");
}

function display_filetree(path, fileinfo_list) {
    // split path components
    var path_components = path.split("/");
    var current_node = filetree_root_node;
    for (let i = 0; i < path_components.length; i++) {
        let filename = path_components[i];
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

function display_filecontent(filepath) {
    // TODO
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
    var request = new XMLHttpRequest();
    request.addEventListener("load", function (event) {
        let response_obj = JSON.parse(event.target.responseText);
        display_filetree(path, response_obj.fileinfo_list);
    });

    let form_data = {
        'filepath': normalize_filepath(path),
    };

    let encoded_form_data_str = serialize_form_fields(form_data);

    request.open('POST', '/listdirectory');
    request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    request.send(encoded_form_data_str);
}

function main() {
    // send a request to get the file tree
    get_directory_info("/");
}

window.onload = main;
