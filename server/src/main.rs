use std::fmt::{Display, Formatter};
use std::io::{BufWriter, Write};
use std::net::{Shutdown, TcpListener, TcpStream};
use std::sync::{Arc, mpsc, Mutex};
use std::sync::mpsc::{Sender, TryRecvError};
use std::time::Duration;

use threadpool::ThreadPool;
use utf8_read::Reader;

use crate::Action::{NoAction, SessionEnd, Terminate};

fn main() {
    let listener = TcpListener::bind("127.0.0.1:7878").unwrap();
    listener.set_nonblocking(true).expect("Error setting nonblocking mode");
    let pool = ThreadPool::new(2);
    let (tx, rx) = mpsc::channel();
    for stream in listener.incoming() {
        match stream {
            Ok(stream) => {
                let tx = tx.clone();
                if pool.active_count() != pool.max_count() {
                    pool.execute(move|| {
                        handle_connection(stream, tx);
                    });
                } else {
                    let stream = stream;
                    let mut writer = BufWriter::new(&stream);
                    writer.write("ERROR OUT_OF_RESS\n".as_bytes()).expect("Error writing to stream");
                    writer.flush().expect("Error flushing");
                    stream.shutdown(Shutdown::Both).unwrap();
                }
            }
            Err(_) => {
                match rx.try_recv() {
                    Ok(_) => {
                        break;
                    }
                    _ => {}
                }
            }
        }
    }
    println!("Waiting for Clients to finish");
    for stream in listener.incoming() {
        match stream {
            Ok(_) => {
                let stream = stream.unwrap();
                let mut writer = BufWriter::new(&stream);
                writer.write("ERROR TERMINATING\n".as_bytes()).expect("Error writing to stream");
                writer.flush().expect("Error flushing");
                stream.shutdown(Shutdown::Both).unwrap();
            }
            Err(_) => {}
        }
        if pool.active_count() < 1 {
            break;
        }
    }
    println!("Clients finished, shutdown");

}

fn handle_connection(stream: TcpStream, channel: Sender<Response>) {
    stream.set_nonblocking(false).expect("Error setting nonblock");
    let mut reader = Reader::new(&stream);
    let mut writer = BufWriter::new(&stream);
    stream.set_read_timeout(Some(Duration::new(30, 0))).expect("Error setting read timeout");
    loop {
        if reader.eof()  {
            break;
        }
        let mut byte_counter = 0;
        let mut message = String::new();
        for char in reader.into_iter() {
            match char {
                Ok(char) => {
                    if (byte_counter > 255) {
                        break;
                    }
                    if char == '\n' && message.len() > 0 {
                        break;
                    } else if char == '\n' && message.len() == 0 {
                        continue;
                    };
                    message.push(char);
                    byte_counter = byte_counter + char.len_utf8();
                }
                Err(_) => return
            }
        }
        if (message.len() != 0) {
            let response = process_message(message);
            writer.write(response.to_string().as_bytes()).unwrap();
            writer.flush().unwrap();
            match response.action {
                Terminate => { channel.send(response).expect("Error syncing response") }
                _ => {}
            }

        }
    }
}

fn process_message(message: String) -> Response {
    return if message.starts_with("LOWERCASE") {
        Response {
            action: NoAction,
            error: false,
            message: lowercase(message.strip_prefix("LOWERCASE").unwrap().to_string())
        }
    } else if message.starts_with("UPPERCASE") {
        Response {
            action: NoAction,
            error: false,
            message: uppercase(message.strip_prefix("UPPERCASE").unwrap().to_string())
        }
    } else if message.starts_with("REVERSE") {
        Response {
            action: NoAction,
            error: false,
            message: reverse(message.strip_prefix("REVERSE").unwrap().to_string())
        }
    } else if message.starts_with("BYE") {
        Response {
            action: SessionEnd,
            error: false,
            message: "BYE".to_string()
        }
    } else if message.starts_with("SHUTDOWN") {
        if message.trim() == "SHUTDOWN HAW" {
            Response {
                action: Terminate,
                error: false,
                message: "SHUTDOWN".to_string()
            }
        } else {
            Response {
                action: NoAction,
                error: true,
                message: "PWD_INCORRECT".to_string()
            }
        }
    } else {
        Response {
            action: NoAction,
            error: true,
            message: "UNKNOWN_CMD".to_string()
        }
    }
}

struct Response {
    action: Action,
    error: bool,
    message: String
}

impl Display for Response {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        let x = if self.error { "ERROR" } else { "OK" };
        write!(f, "{} {}\n", x, self.message.trim())
    }
}

enum Action {
    NoAction,
    SessionEnd,
    Terminate,
}

fn lowercase(string: String) -> String {
    return string.to_lowercase();
}

fn uppercase(string: String) -> String {
    return string.to_uppercase();
}

fn reverse(string: String) -> String {
    return string.chars().rev().collect::<String>()
}
