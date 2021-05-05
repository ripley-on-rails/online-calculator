# online-calculator

A simple webservice calculator that accepts BASE64 encoded input of a mathematical
infix expression. It support addition (+), subtraction (-), multiplication (*),
division (/), parenthesis, and integers. The evaluation uses PEMDAS/BODMAS precedence
and left-to-right evaluation in case of equal precendces of operators.

It will return three possible responses:
* `{"error":true,"message":"division by zero"}` in case there is a division by zero ...duh
* `{"error":true,"message":"invalid expression"}` if the expression could not be parsed
* `{"error":false,"result":<result>}` in case of a successful evaluation

## Usage

### Starting the web-app

Either run `lein run` in the project or repl via `lein repl` and execute `(start!)` in
the `online-calculator.core` namespace in order to start the server.

The default port `7777`. It can be passed as via repl as an argument like `(start! 1234)`
or as the first argument via java command-line `java -jar <path to jar> 1234` or via the
system environment variable `PORT`.

Make a test call to the url `http://localhost:7777/calculus?query=MiAqICgyMy8oMyozKSktIDIzICogKDIqMyk` and it should return a json response `{"error":false,"result":-132.8888888888889}`.

###

## License

Copyright © 2021 Ripley Flammer
