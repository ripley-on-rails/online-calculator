(ns online-calculator.parse-test
  (:require [clojure.test :refer :all]
            [online-calculator.parse :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check :as tc]))

;; test ()

(deftest invalid-query-tests
  (testing "Invalid queries"
    (are [expected expression] (= expected (solve (parse expression)))
      {:error "invalid expression"} "1 2"
      {:error "invalid expression"} "1 + + 2"))
  (testing "division by zero"
    (are [expected expression] (= expected (solve (parse expression)))
      {:error "division by zero"} "1/0"
      {:error "division by zero"} "1/(2-2)"))
  (testing "queries"
    (are [expected expression] (= expected (solve (parse expression)))
      -17411/33  "2 * (23/(33))- 23 * (23)"
      -1196/9    "2 * (23/(3*3))- 23 * (2*3)"))
  (testing "Regressions"
    (are [expected expression] (= expected (solve (parse expression)))
      -347 "(3 / 1 * 2) + ( -123 - -1 * 2)*3 +  10 / 1"
      -121 "( -123 - -1 * 2)"
      -1   "0+0- 0+-1"
      -13  "(-1)+54-                    65                        +-1")))

(def whitespaces
  (gen/bind gen/small-integer
            (fn [n]
              (gen/return (apply str (repeat n " "))))))

(def whitespace-attr
  (gen/let [leading whitespaces
            trailing whitespaces]
    (gen/return {:leading leading
                 :trailing trailing})))

(def number
  (gen/tuple (gen/return :integer)
             gen/small-integer
             whitespace-attr))

(defn parenthesis [inner-gen]
  (gen/tuple (gen/return :parenthesis)
             inner-gen
             whitespace-attr
             whitespace-attr))

(defn operation [inner-gen]
  (gen/such-that (fn [[op [type-arg1] [type-arg2] :as x]]
                   (and
                    ;; prevent addition/subtraction child nodes to be generated
                    ;; when the parent node is a multiplication or division.
                    ;; This would otherwise generate trees ignoring operator precedence.
                    (not (and (#{:* :/} op)
                              (some #{:+ :-} [type-arg1 type-arg2])))
                    ;; due to left-to-right rule same order precedence must unfold to the left
                    ;; such as ((a * b) * c) * d.
                    ;; Here we are preventing cases such as 'a * (b * c)' or 'a * (b + c)'
                    (not (and (#{:* :/} op)
                              (#{:* :/ :+ :-} type-arg2)))
                    ;; Here we are preventing cases such as 'a - (b + c)' "a-b+c"
                    (not (and (#{:+ :-} op)
                              (#{:+ :-} type-arg2)))))
                 (gen/tuple (gen/elements [:+ :- :* :/])
                            inner-gen inner-gen)))

(def expression
  (fn [inner-gen]
    (gen/one-of [number
                 (parenthesis inner-gen)
                 (operation inner-gen)])))

(def expression-rec
  (gen/recursive-gen expression number))

(declare tree->query)

(defn- error [v]
  (and (map? v)
       (v :error)
       v))

(defn op->query [op [a b]]
  (let [[s1 v1] (tree->query a)
        [s2 v2] (tree->query b)
        f (case op
            :+ +
            :- -
            :* *
            :/ /)
        error (or (error v1)
                  (error v2)
                  (if (and (= v2 0)
                           (= op :/))
                    {:error "division by zero"}))]
    [(str s1 (name op) s2) (or error (f v1 v2))]))

(defn- wrap-with-whitespace [s {:keys [leading trailing]}]
  (str leading s trailing))

(defn tree->query [[op & rest :as tree]]
  (case op
    :integer (let [v (first rest)
                   ws (last rest)]
               [(wrap-with-whitespace (str v) ws) v])
    :parenthesis (let [[s v] (tree->query (first rest))
                       [ws1 ws2] (take-last 2 rest)]
                   [(str (wrap-with-whitespace "(" ws1)
                         s
                         (wrap-with-whitespace ")" ws2)) v])
    :- (op->query op rest)
    :+ (op->query op rest)
    :* (op->query op rest)
    :/ (op->query op rest)))

(defspec test-solve-and-parse 100
  (prop/for-all [exp expression-rec]
                (let [[query expected] (tree->query exp)]
                  (= expected
                     (solve (parse query))))))

;;(tc/quick-check 100 test-solve-and-parse)
