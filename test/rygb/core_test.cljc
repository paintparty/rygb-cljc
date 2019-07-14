(ns rygb.core-test
  (:require
    #?(:cljs [cljs.test :refer-macros [is deftest testing]]
       :clj  [clojure.test :refer [is deftest testing]])
   [clojure.repl :refer [demunge]]
   [clojure.string :as string]
   ;[clojure.pprint :refer [pprint]]
   [rygb.core :as rygb]))

(defn fname [f]
  (-> f
      str
      demunge
      (string/split #"@")
      first
      (string/split #"/")
      last))

(defn styled-bg [s code]
  (str "\033[48;5;" code "m " s "\033[0m"))

(def green-checkmark "\033[38;5;40m  ✔ \033[0m")

(def fns
  [rygb/rygb->string
   rygb/rygb->map
   rygb/rygb->hex
   rygb/rygb->hexa
   rygb/rygb->int24
   rygb/rygb->rgb
   rygb/rygb->rgb-bit
   rygb/rygb->rgb-css
   rygb/rygb->rgba
   rygb/rygb->rgba-bit
   rygb/rygb->rgba-css
   rygb/rygb->argb
   rygb/rygb->argb-bit])

(def colors
  [["red-yellow"
    {:terminal 214
     :args ["ry" "yr" {:h {:y 1 :r 1}}]
     :expected ["ry"
                {:h {:r 1, :y 1}}
                "#ff8000"
                "#ff8000ff"
                16744448
                [1.0 0.5 0.0]
                [255 128 0]
                "rgb(255, 128, 0)"
                [1.0 0.5 0.0 1.0]
                [255 128 0 255]
                "rgba(255, 128, 0, 1.0)"
                [1.0 1.0 0.5 0.0]
                [255 255 128 0]]}]

   ["yellow-green"
    {:terminal 148
     :args ["yg" "gy" {:h {:y 1 :g 1}}]
     :expected ["yg"
                {:h {:y 1, :g 1}}
                "#80ff00"
                "#80ff00ff"
                8453888
                [0.5 1.0 0.0]
                [128 255 0]
                "rgb(128, 255, 0)"
                [0.5 1.0 0.0 1.0]
                [128 255 0 255]
                "rgba(128, 255, 0, 1.0)"
                [1.0 0.5 1.0 0.0]
                [255 128 255 0]]}]

   ["green-blue"
    {:terminal 51
     :args ["gb" "bg" {:h {:g 1 :b 1}}]
     :expected ["gb"
                {:h {:g 1, :b 1}}
                "#00ffff"
                "#00ffffff"
                65535
                [0.0 1.0 1.0]
                [0 255 255]
                "rgb(0, 255, 255)"
                [0.0 1.0 1.0 1.0]
                [0 255 255 255]
                "rgba(0, 255, 255, 1.0)"
                [1.0 0.0 1.0 1.0]
                [255 0 255 255]]}]

   ["blue-red"
    {:terminal 201
     :args ["br" "rb" {:h {:b 1 :r 1}}]
     :expected ["br"
                {:h {:r 1, :b 1}}
                "#ff00ff"
                "#ff00ffff"
                16711935
                [1.0 0.0 1.0]
                [255 0 255]
                "rgb(255, 0, 255)"
                [1.0 0.0 1.0 1.0]
                [255 0 255 255]
                "rgba(255, 0, 255, 1.0)"
                [1.0 1.0 0.0 1.0]
                [255 255 0 255]]}]

   ["red"
    {:terminal 160
     :args ["r" {:h {:r 1}}]
     :expected ["r"
                {:h {:r 1}}
                "#ff0000"
                "#ff0000ff"
                16711680
                [1.0 0.0 0.0]
                [255 0 0]
                "rgb(255, 0, 0)"
                [1.0 0.0 0.0 1.0]
                [255 0 0 255]
                "rgba(255, 0, 0, 1.0)"
                [1.0 1.0 0.0 0.0]
                [255 255 0 0]]}]

   ["yellow"
    {:terminal 226
     :args ["y" {:h {:y 1}}]
     :expected ["y"
                {:h {:y 1}}
                "#ffff00"
                "#ffff00ff"
                16776960
                [1.0 1.0 0.0]
                [255 255 0]
                "rgb(255, 255, 0)"
                [1.0 1.0 0.0 1.0]
                [255 255 0 255]
                "rgba(255, 255, 0, 1.0)"
                [1.0 1.0 1.0 0.0]
                [255 255 255 0]]}]

   ["green"
    {:terminal 40
     :args ["g" {:h {:g 1}}]
     :expected ["g"
                {:h {:g 1}}
                "#00ff00"
                "#00ff00ff"
                65280
                [0.0 1.0 0.0]
                [0 255 0]
                "rgb(0, 255, 0)"
                [0.0 1.0 0.0 1.0]
                [0 255 0 255]
                "rgba(0, 255, 0, 1.0)"
                [1.0 0.0 1.0 0.0]
                [255 0 255 0]]}]

   ["blue"
    {:terminal 21
     :args ["b" {:h {:b 1}}]
     :expected ["b"
                {:h {:b 1}}
                "#0000ff"
                "#0000ffff"
                255
                [0.0 0.0 1.0]
                [0 0 255]
                "rgb(0, 0, 255)"
                [0.0 0.0 1.0 1.0]
                [0 0 255 255]
                "rgba(0, 0, 255, 1.0)"
                [1.0 0.0 0.0 1.0]
                [255 0 0 255]]}]


   ["robin's egg blue"
    {:terminal 116
     :args ["gb-s21-v83" "bg-s21-v83" {:h {:g 1, :b 1}, :s 0.21, :v 0.83}]
     :expected ["gb-s21-v83"
                {:h {:g 1, :b 1}, :s 0.21, :v 0.8300000000000001}
                "#a7d4d4"
                "#a7d4d4ff"
                10998996
                [0.6557000000000001 0.8300000000000001 0.8300000000000001]
                [167 212 212]
                "rgb(167, 212, 212)"
                [0.6557000000000001 0.8300000000000001 0.8300000000000001 1.0]
                [167 212 212 255]
                "rgba(167, 212, 212, 1.0)"
                [1.0 0.6557000000000001 0.8300000000000001 0.8300000000000001]
                [255 167 212 212]]}]

   ["50% gray, 33% transparent"
    {:terminal 252
     :args ["v50-a33" {:v 0.5 :a 0.33}]
     :expected ["v50-a33"
                {:v 0.5, :a 0.33}
                "#808080"
                "#80808054"
                8421504
                [0.5 0.5 0.5]
                [128 128 128]
                "rgb(128, 128, 128)"
                [0.5 0.5 0.5 0.33]
                [128 128 128 84]
                "rgba(128, 128, 128, 0.33)"
                [0.33 0.5 0.5 0.5]
                [84 128 128 128]]}]])

(defn fn-and-result [fname arg expected]
  (str "(" fname " \"" arg "\")" " => " expected ))

(deftest basic-colors
  ; pprint a vector of expected results
  ; (pprint (concat [nil] (mapv #(% "br") fns) [nil]))
  (doseq [[color-name {:keys [args expected terminal]}] colors]
    (testing color-name
      (println (str "\n" color-name ":\n" ))
      (doseq [i (-> fns count range)
              :let [f (get fns i)
                    fname (fname f)
                    expected (get expected i)]]
        (doseq [arg args]
          (let [result (fn-and-result fname arg expected)]
            (is (= (f arg) expected)
                (str "Failed -> "  result))))
        (when (every? #(= (f %) expected) args)
          (println (str (styled-bg "  " terminal) "  " fname  green-checkmark)))))))

(deftest returns-nil?
  (testing "bad input"
    (is (every? nil? (map #(% "xb") fns))))
  (testing "blank string"
    (is (every? nil? (map #(% " ") fns))))
  (testing "empty map"
    (is (every? nil? (map #(% {}) fns)))))
