(ns rygb.core
  (:require
   [clojure.string :as string]))

(def floor #?(:cljs js/Math.floor :clj #(Math/floor  %)))
(def round #?(:cljs js/Math.round :clj #(Math/round (double %))))
(def abs #?(:cljs js/Math.abs :clj #(Math/abs %)))
(def parse-int #?(:cljs js/parseInt :clj #(when-not (nil? %) (Integer/parseInt %))))
(def int->string
  #?(:clj #(Integer/toString ^Long % 16))
  #?(:cljs #(.toString % 16)))
(def hue-re "^((([rygb])(\\d*))(([rygb])(\\d*))?)")
(def sva-re "(-([sva])(100?|[\\d]{1,2}))")
(def val-re "^v(100?|[\\d]{1,2})(-a(100?|[\\d]{1,2}))?")
(def rygb-re (re-pattern (str hue-re sva-re "?" sva-re "?" sva-re "?" "$")))
(def pair-re #"^(?:r|y|g|b|ry|yr|yg|gy|gb|bg|br|rb)$")

(defn- hue-vec [m]
  (let [revs {'(:r :b) [:b :r]
              '(:y :r) [:r :y]
              '(:g :y) [:y :g]
              '(:b :g) [:g :b]}
        [h1 h2*] (or (-> m keys revs) (keys m))
        h2 (or h2* h1)]
    [h1 (h1 m) h2 (h2 m)]))

(defn- hsl-s [s v hsl-l]
  (if (= 0 v)
    0
    (round (/ (* s v)
              (if (< hsl-l 50)
                (* 2 hsl-l)
                (- 200 (* hsl-l 2)))))))

(defn- sla [{s :s v :v a :a :or {s 100 v 100 a 100}}]
  (let [sat (if (> s 100) 100 s)
        val (if (> v 100) 100 v)
        hsl-l (round (* (/ val 2) (- 2 (/ sat 100))))]
    [(hsl-s val sat hsl-l)
     hsl-l
     #?(:cljs (/ a 100) :clj (/ (double a) 100))]))

(defn- hue-map->angle [{:keys [r b] :as m}]
  (let [[h1 h1v h2 h2v] (hue-vec m)
        red-pos (if (and r b) 360 0)
        hue-pos {:r red-pos :y 60 :g 120 :b 240}
        rel-pos (- 1 (/ h1v (+ h1v h2v)))
        abs-diff (abs (- (h1 hue-pos) (h2 hue-pos)))
        hue (round (+
                    (h1 hue-pos)
                    (* (double rel-pos) abs-diff)))]
    hue))

(defn- hsva->rgba
  [{:keys [h s v a] :or {h 0.0 s 1.0 v 1.0 a 1.0}}]
  (let [h (-> h hue-map->angle (/ 360))
        i (floor (* h 6))
        f (- (* h 6) i)
        p (* v (- 1 s))
        q (* v (- 1 (* f s)))
        t (* v (- 1 (* (- 1 f) s)))
        m (round (mod i 6))
        rgb [[v t p] [q v p] [p v t] [p q v] [t p v] [v p q]]
        [r g b] (get rgb m)]
    [r g b a]))

(defn- va->rgba [v* a]
  (let [v (-> v* parse-int (/ 100) (* 255) (* 100) round (/ 100) int)
        alpha (if a (-> a parse-int (* 0.01)) 1)]
    [v v v alpha]))

(defn- va [v* a]
  (let [v (-> v* parse-int (* 0.01))
        alpha (if a (-> a parse-int (* 0.01)) 1)]
    {:rygb-map {:v v :a alpha} :rgba [v v v alpha]}))

(defn- hue-map [m]
  (let  [hue-m* (select-keys m [:r :y :g :b])]
    (if (= (keys hue-m*) [:r :b])
      {:b (:b m) :r (:r m)}
      (into {} (for [[k v] hue-m*]
                 [k (if (nil? v) 1 v)])))) )

(defn- hsva* [& coll]
  (let [argz (remove nil? coll)
        vals (take-nth 2 (rest argz))
        keyz (map keyword (take-nth 2 argz))
        all (zipmap keyz vals)
        sva-m* (select-keys all [:s :v :a])
        sva-m (into {} (for [[k v] sva-m*]
                         [k (* v 0.01)]))]
    (merge {:h (hue-map all)} sva-m)))

(defn- hv [h hv]
  (when h
    (cond (string/blank? hv) 1
          (= "0" hv) 1
          :else (parse-int hv))))

(defn- valid-svas? [v]
  (or (every? nil? v)
      (apply distinct? (remove nil? v))))

(defn- valid-hues? [h1 h2]
  (if (re-matches pair-re (str h1 h2)) true false))

(defn- hsva [matches]
  (let [[_ _ _ h1 h1v* _ h2 h2v* _ x1 x1v* _ x2 x2v* _ x3 x3v*] matches
        h1v (hv h1 h1v*)
        h2v (hv h2 h2v*)
        [x1v x2v x3v] (map parse-int [x1v* x2v* x3v*])
        valid-hues? (valid-hues? h1 h2)
        valid-svas? (or (every? nil? [x1 x2 x3])
                        (apply distinct? (remove nil? [x1 x2 x3])))]
    (when (and valid-hues? valid-svas?)
      (hsva* h1 h1v h2 h2v x1 x1v x2 x2v x3 x3v))))

(defn- rygb-hue-map->string [m]
  (let [[h1 h1v*] (first m)
        [h2 h2v*] (second m)
        h1v (when (< 1 h1v*) h1v*)
        h2v (when (and h2 (< 1 h2v*)) h2v*)]
    (str (when h1 (name h1)) h1v (when h2 (name h2)) h2v)))

(defn- kv->string [acc k v]
  (let [s (if (= k :h)
            (rygb-hue-map->string v)
            (str (name k) (-> (* v 100) int str)))]
    (conj acc s)))

(defn- rygb-map->rygb-string [m]
  (->> m
       (reduce-kv kv->string [])
       (string/join "-")))

(defn- rygb* [arg]
  "Converts a string or map in valid rygb syntax to an rygb data structure"
  (cond
    (string? arg)
    (if-let [[_ v _ a] (re-matches (re-pattern val-re) arg)]
      (assoc (va v a) :string arg)
      (if-let [matches (re-matches rygb-re (string/lower-case arg))]
        (let [rygb-map (hsva matches)]
          {:rygb-map rygb-map
           :string (rygb-map->rygb-string rygb-map)
           :rgba nil})))
    (map? arg)
    (-> arg rygb-map->rygb-string rygb*)
    :else nil))

(defn- rgba [rygb*]
  (or (:rgba rygb*) (hsva->rgba (:rygb-map rygb*))))

(defn- rgb [rygb*]
  (let [[r g b _] (rgba rygb*)]
    [r g b]))

(defn- argb [[r g b a]]
  [a r g b])

(defn- decimal->bytes [n]
  (round (* n 255)))

(defn bit-vec [coll]
  (->> coll (map decimal->bytes) (into [])))

(defn- c->hex [c]
  (let [hex (int->string c)]
    (if (= 1 (count hex))
      (str "0" hex)
      hex)))

(defn- hex* [coll]
  (->> coll
       (map c->hex)
       string/join
       (str "#")))

(defn- rgb-a-css [[r g b a] a?]
  (let [bit (map decimal->bytes [r g b])
        args (if a?
               (conj (vec bit) a)
               bit)]
    (str (if a? "rgba" "rgb") "(" (string/join ", " args) ")")))

(defn- rgba-css [rgba-coll]
  (rgb-a-css rgba-coll true))

(defn- rgb-css [rgb-coll]
  (rgb-a-css rgb-coll false))

(defn int24 [rgb]
  (let [[r g b] rgb]
    (bit-or (bit-shift-left r 16) (bit-shift-left g 8) b)))

; API
(defn rygb->string [arg]
  (some-> arg rygb* :string))

(defn rygb->map [arg]
  (some-> arg rygb* :rygb-map))

(defn rygb->rgba [arg]
  (some-> arg rygb* rgba))

(defn rygb->rgba-bit [arg]
  (some-> arg rygb->rgba bit-vec))

(defn rygb->rgba-css [arg]
  (some-> arg rygb->rgba rgba-css))

(defn rygb->rgb [arg]
  (some-> arg rygb* rgb))

(defn rygb->rgb-css [arg]
  (some->> arg rygb->rgb rgb-css))

(defn rygb->rgb-bit [arg]
  (some->> arg rygb->rgb bit-vec))

(defn rygb->argb [arg]
  (some-> arg rygb->rgba argb))

(defn rygb->argb-bit [arg]
  (some-> arg rygb->argb bit-vec))

(defn rygb->hexa [arg]
  (some-> arg rygb->rgba-bit hex*))

(defn rygb->hex [arg]
  (some-> arg rygb->rgb-bit hex*))

(defn rygb->int24 [arg]
  (some-> arg rygb->rgb-bit int24))
