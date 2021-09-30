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

function get_directory_info(path) {
    path = normalize_filepath(path);
    var request = new XMLHttpRequest();
    request.addEventListener("load", function (event) {
        let response_obj = JSON.parse(event.target.responseText);
        display_filetree(path, response_obj.fileinfo_list);
    });

    let request_path = `/listdirectory/${path}`;
    request_path = normalize_filepath(request_path);

    request.open("GET", request_path);
    request.send();
}

function main() {
    // send a request to get the file tree
    get_directory_info("/");
}

window.onload = main;
