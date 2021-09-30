# create my own http server
import os
import socket
import socketserver
import threading
import traceback
import typing
import time


def build_http_response_header(response_code: int, content_type: str, content_length: int):
    response_code_str = repr(response_code)

    response_header = f'HTTP/1.1 ${response_code_str}\r\n'
    response_header += f'Content-Type: ${content_type}\r\n'
    response_header += f'Content-Length: ${content_length}\r\n'

    return response_header


def read_socket_thread_function(socket_obj: socket.socket, buffer_size: int, content_list: typing.List[bytes]):
    buffer = socket_obj.recv(buffer_size)
    content_list.append(buffer)


class HttpRequestParsingState:
    READING_METHOD = 1
    READING_URL = 2
    READING_HTTP_VERSION = 3
    READING_HEADER_NAME = 4
    READING_HEADER_VALUE = 5
    DONE_READING_HEADERS = 6


class UnhandledRequestContentListIterator:
    def __init__(self, unhandled_content_list: typing.List[bytes]):
        self.unhandled_content_list = unhandled_content_list
        self.list_index = 0
        self.byte_index = 0
        self.remaining_bytes = 0
        for content in self.unhandled_content_list:
            self.remaining_bytes += len(content)
        self.read_bytes = 0

    def read_next_byte(self):
        if len(self.unhandled_content_list) == 0:
            return -1

        if self.list_index >= len(self.unhandled_content_list):
            return -1

        content_bs = self.unhandled_content_list[self.list_index]
        if len(content_bs) == 0:
            self.list_index += 1
            self.byte_index = 0
            return self.read_next_byte()

        if self.byte_index >= len(content_bs):
            self.list_index += 1
            self.byte_index = 0
            return self.read_next_byte()

        byte_value = content_bs[self.byte_index]
        self.byte_index += 1
        self.read_bytes += 1
        self.remaining_bytes -= 1
        return byte_value

def is_valid_http_method_char(byte_value: int):
    return byte_value >= ord('A') and byte_value <= ord('Z')

class HttpRequest:
    def __init__(self, clientsocket: socket.socket, clientaddress: str):
        self.clientsocket = clientsocket
        self.clientaddress = clientaddress
        self.handled_request_content_list: typing.List[bytes] = []
        self.unhandled_request_content_list: typing.List[bytes] = []
        self.request_parsing_state = HttpRequestParsingState.READING_METHOD
        self.request_method: str = None
        self.request_url: str = None
        self.cache_object = {}

    def has_all_request_headers(self):
        if self.request_parsing_state == HttpRequestParsingState.DONE_READING_HEADERS:
            return True

        bs_iterator = UnhandledRequestContentListIterator(self.unhandled_request_content_list)

        byte_value = bs_iterator.read_next_byte()
        while byte_value >= 0:
            if self.request_parsing_state == HttpRequestParsingState.READING_METHOD:
                if is_valid_http_method_char(byte_value):
                    if not 'cached_http_method_char_list' in self.cache_object:
                        self.cache_object['cached_http_method_char_list'] = [byte_value]
                    else:
                        self.cache_object['cached_http_method_char_list'].append(byte_value)
                else:
                    if
            # TODO
            byte_value = bs_iterator.read_next_byte()


        if self.request_parsing_state == HttpRequestParsingState.DONE_READING_HEADERS:
            return True
def handle_socket_client(client_socket: socket.socket, client_address):
    http_request = HttpRequest(client_socket, client_address)

    # TODO read the request header until there are two new lines
    timeout_for_each_read = 1
    wait_time_spacing = 0.1
    number_of_wait_loop = int(timeout_for_each_read / wait_time_spacing)
    buffer_size = 1024

    new_line_count = 0

    # TODO repeat these steps multiple times and parsing response on the fly
    read_content_list = []
    ####################################################################
    tmp_buffer_content_list = []
    try:
        read_thread = threading.Thread(target=read_socket_thread_function, args=(client_socket, buffer_size, tmp_buffer_content_list))
        read_thread.start()
        for _ in range(number_of_wait_loop):
            if not read_thread.is_alive():
                break
            time.sleep(wait_time_spacing)
        if read_thread.is_alive():
            # TODO kill the read thread
            read_thread.kill()
            pass  # TODO handle error
    except Exception as ex:
        print(ex)
        stack_trace_str = traceback.format_exc()
        print(stack_trace_str)
    if len(tmp_buffer_content_list) == 0:
        # TODO handle error
        raise Exception('No content received')
    new_content_bs = b''.join(tmp_buffer_content_list)
    read_content_list.append(new_content_bs)
    # TODO parse the available content
    ####################################################################
    try:
        client_socket.shutdown(socket.SHUT_WR)
    except Exception as ex:
        stack_trace_str = traceback.format_exc()
        print(ex)
        print(stack_trace_str)
    pass


def createHttpServer(host: str, port: int):
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        server_socket.bind((host, port))
        server_socket.listen(16)

        while True:
            (client_socket, client_address) = server_socket.accept()
            client_thread = threading.Thread(target=handle_socket_client, args=(client_socket, client_address,))
            client_thread.start()
    except Exception as ex:
        stack_trace_str = traceback.format_exc()
        print(ex)
        print(stack_trace_str)

    try:
        server_socket.close()
    except Exception as ex:
        stack_trace_str = traceback.format_exc()
        print(ex)
        print(stack_trace_str)
