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
the `online-calculator.core` namespace in order to start the server. Alternatively build
a jar file via `lein uberjar` and run it via `java - jar <path to jar>`.

The default port `8080`. It can be passed as via repl as an argument like `(start! 1234)`
or as the first argument via java command-line `java -jar <path to jar> 1234` or via the
system environment variable `PORT`.

Make a test call to the url `http://localhost:8080/calculus?query=MiAqICgyMy8oMyozKSktIDIzICogKDIqMyk` and it should return a json response `{"error":false,"result":-132.8888888888889}`.

## Deployment

The web service is currently deployed to Heroku. A test call can be made to
`https://ripleys-online-calculator.herokuapp.com/calculus?query=MiAqICgyMy8oMyozKSktIDIzICogKDIqMyk`.

To deploy obtain and setup the project:

> heroku login
>
> git clone https://github.com/phalphalak/online-calculator.git
>
> cd online-calculator
>
> heroku create

This will give you something like `sheltered-harbor-57189` for instance

In order to deploy:

> git push heroku master

Now the service should be accessible via `https://sheltered-harbor-57189.herokuapp.com/calculus?query=MiAqICgyMy8oMyozKSktIDIzICogKDIqMyk` in our example.

## License

Copyright © 2021 Ripley Flammer
