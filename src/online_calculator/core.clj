(ns online-calculator.core
  (:require [instaparse.core :as insta]))


(def parse
  (insta/parser
   "

<EXP> = <SPACES?> ((ADDITION / MULTIPLICATION) | PARENTHESIS | NUMBER) <SPACES?>

SPACES = #'\\s'*
<NUMBER> = <SPACES?> INTEGER <SPACES?>
INTEGER = ((#'-*[1-9][0-9]*') | '0')
PARENTHESIS = <SPACES?> <'('> EXP <')'> <SPACES?>

MULTIPLICATION = <SPACES?> (MULTIPLICATION | NUMBER | PARENTHESIS) ('*'|'/') (NUMBER | PARENTHESIS) <SPACES?>
ADDITION = <SPACES?> (ADDITION | NUMBER | PARENTHESIS | MULTIPLICATION) (#'\\+|-') (NUMBER | PARENTHESIS |  MULTIPLICATION ) <SPACES?>


"
   ;; :output-format :enlive
   ))


(defn- solve* [[type & rest]]
  (case type
    :INTEGER (let [value (first rest)]
               (Integer/parseInt value))
    :ADDITION (let [[a op b] rest
                    a (solve* a)
                    b (solve* b)]
                (case op
                  "+" (+ a b)
                  "-" (- a b)))
    :PARENTHESIS (solve* (first rest))
    :MULTIPLICATION (let [[a op b] rest
                          a (solve* a)
                          b (solve* b)]
                      (case op
                        "*" (* a b)
                        "/" (/ a b)))))

(defn solve [tree]
  (solve* (first tree)))
