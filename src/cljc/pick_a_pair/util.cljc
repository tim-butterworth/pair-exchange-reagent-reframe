(ns pick-a-pair.util)

(defn zip-list [list1 list2]
  (loop [remainder1 list1
         remainder2 list2
         accume []]
    (if
        (or
         (empty? remainder1)
         (empty? remainder2))
      accume
      (recur
       (rest remainder1)
       (rest remainder2)
       (conj accume [(first remainder1) (first remainder2)])))))

(defn make-indentation [depth]
  (apply str (repeat depth "  ")))

(declare pretty-to-string)

(defn symbol-wrap [{open :open close :close} indent body]
  (let [close-indentation (make-indentation indent)]
    (str
     (str open "\n")
     body
     (str "\n" close-indentation close))))

(defn pretty-list [list indent]
  (let [indentation (make-indentation (inc indent))]
    (str
     (symbol-wrap
      {:open "[" :close "]"}
      indent
      (clojure.string/join
       (str "\n")
       (map
        (fn [entry]
          (str indentation (pretty-to-string entry (inc indent))))
        list))))))

(defn pretty-map [obj indent]
  (let [key-list (keys obj)
        value-list (vals obj)
        indentation (make-indentation (inc indent))
        close-indentation (make-indentation indent)]
    (symbol-wrap
     {:open "{" :close "}"}
     indent
     (clojure.string/join
      "\n"
      (map
       (fn [entry]
         (let [[key value] entry]
           (str indentation key ", " (pretty-to-string value (inc indent)))))
       (zip-list key-list value-list))))))

(defn pretty-to-string
  ([obj]
   (pretty-to-string obj 0))
  ([obj indent]
   (cond (sequential? obj) (pretty-list obj indent)
         (map? obj) (pretty-map obj indent)
         :default obj)))
