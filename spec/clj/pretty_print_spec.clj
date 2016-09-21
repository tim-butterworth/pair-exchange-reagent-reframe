(ns pretty-print-spec
  (:require [speclj.core :refer :all]
            [pick-a-pair.util :as util]))

(defn print-and-return [value]
  (do
    (println value)
    value))

(describe "pretty-to-string"

          (describe "primitive structures"
                    (it "formats a list"
                        (should=
                         "[\n  a\n  b\n  c\n]"
                         (print-and-return
                          (util/pretty-to-string ["a" "b" "c"]))))

                    (it "formats a string"
                        (should=
                         "super"
                         (util/pretty-to-string "super")))

                    (it "formats a map"
                        (should=
                         (str "{\n  " :key ", " :value "\n  " :key2 ", " :value2 "\n}")
                         (print-and-return
                          (util/pretty-to-string {:key :value :key2 :value2})))))

          (describe "nested structurs"

                    (it "formats lists of lists"
                        (should=
                         (clojure.string/join
                          "\n"
                          ["["
                           "  ["
                           "    2"
                           "    b"
                           "  ]"
                           "  ["
                           "    ["
                           "      c"
                           "      d"
                           "    ]"
                           "  ]"
                           "]"])
                         (print-and-return
                          (util/pretty-to-string [[2 "b"] [["c" "d"]]]))))

                    (it "formats maps of maps"
                        (should=
                         (clojure.string/join
                          "\n"
                          ["{"
                           "  :a, a"
                           "  :c, {"
                           "    :d, d"
                           "    :f, {"
                           "      :g, g"
                           "    }"
                           "  }"
                           "}"])
                         (print-and-return
                          (util/pretty-to-string {:a "a" :c {:d "d" :f {:g "g"}}}))))))
