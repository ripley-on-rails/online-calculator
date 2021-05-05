# online-calculator

A Clojure library designed to ... well, that part is up to you.

## Usage

### Starting the web-app

Either run `lein run` in the project or repl via `lein repl` and execute `(start!)` in
the `online-calculator.core` namespace in order to start the server.

The default port `7777`. It can be passed as via repl as an argument like `(start! 1234)`
or as the first argument via java command-line `java -jar <path to jar> 1234` or via the
system environment variable `PORT`.

## License

Copyright © 2021 Ripley Flammer
