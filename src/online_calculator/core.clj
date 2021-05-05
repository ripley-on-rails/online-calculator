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

(defmacro wrap-error [results & exprs]
  `(if-let [error# (first (filter map? ~results))]
     (prn error#)
     (do ~@exprs)))

(defn- solve-op [[a op b]]
  (let [a (solve* a)
        b (solve* b)]
    (wrap-error [a b]
                (case op
                  "+" (+ a b)
                  "-" (- a b)
                  "*" (* a b)
                  "/" (if (zero? b)
                        {:error "division by zero"}
                        (/ a b))))))

(defn- solve* [[type & rest]]
  (case type
    :INTEGER (let [value (first rest)]
               (Integer/parseInt value))
    :ADDITION (solve-op rest)
    :MULTIPLICATION (solve-op rest)
    :PARENTHESIS (solve* (first rest))))

(defn solve [tree]
  (if (insta/failure? tree)
    {:error "invalid expression"}
    (solve* (first tree))))
