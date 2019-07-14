# RYGB #
RYGB color notation is a syntactical abstraction over the HSV color model. Specifically, it provides an intuitive and analogous approach to expressing hue by way of additive, proportional mixing of adjacent primary colors.

The RYGB chromatic model is based on the [opponent process color theory](https://en.wikipedia.org/wiki/Opponent_process), first theorized by physiologist Ewald Hering in 1892. This model consists of two opposing color axioms - red/green and yellow/blue.  The orthogonal arrangement of these axioms results in 4 adjacent color pairs, each a binary set:

<br>

<p align="center"><img height="300px" src="https://raw.githubusercontent.com/paintparty/rygb-js/master/images/rygb-diagram.svg?sanitize=true" /></p>

&nbsp;
<br>

Any desired hue can be expressed by determining the appropriate pair, then mixing the two associated primaries in relative parts. For example, the notation `'r2y3'` results in a reddish-yellow hue that is exactly 2 parts red and 3 parts yellow.

RYGB was created for coding-centric design/development workflows. It is intended for rapid prototyping, graphic design, illustration, animation, and scenarios when experimental color choices may happen frequently. In such cases, the repeated context-switching inherent with the use of traditional GUI color-selection tools may be both cumbersome and undesirable.

<br>

## Installation ##
Add the following dependency to your `project.clj` file

```clojure
[rygb "0.1.0-SNAPSHOT"]
```
<br>

## Usage ##

Require `rygb.core` in your ns and optionally `:refer` individual functions:
```clojure
(require '[rygb.core :as rygb :refer [rygb->hex]])
```

Equal parts blue and red will produce magenta:
```clojure
(rygb->hex "br")  ;; => "#ff00ff"
```

Equal parts red and yellow will produce orange:
```clojure
(rygb->hex "ry")  ;; => "#ff8000"
```

1 part red, 2 parts yellow:
```clojure
(rygb->hex "ry2")  ;; => "#ffaa00"
```

3 parts red, 7 parts yellow:
```clojure
(rygb->hex "r3y7")  ;; => "ffb300"
```

Order is reversible, although `ry`, `yg`, `gb`, and `br` is idiomatic.
```clojure
(rygb->hex "y7r3") === (rygb->hex "r3y7")  ;; => true
```

When using a single part of a primary, the `1` is optional. Although its omission is idiomatic, it's inclusion is occasionally useful for readability in certain ratios:
```clojure
(rygb->hex "r1y2") === (rygb->hex "ry2")  ;; => true

(rygb->hex "br") === (rygb->hex "b1r1")  ;; => true

(rygb->hex "b23r1") === (rygb->hex "b23r")  ;; => true
```

<br>

**Saturation**, **value**, and **alpha** are each expressed using an integer `[0-100]`, representing a percentage. The default value for each is `100`.

3 parts red, 7 parts yellow, with 50% saturation:
```clojure
(rygb->hex "r3y7-s50") ;; => "#ffd980"
```

3 parts red, 7 parts yellow, with 50% saturation and 33% value:
```clojure
(rygb->hex "r3y7-s50-v33") ;; => "#f4482a"
```

3 parts red, 7 parts yellow, with 50% saturation, 33% value, and 66% alpha:
```clojure
(rygb->hexa "r3y7-s50-v33-a66").hexa()  ;; => "#f4482aa8"
```

<br>

**Primary colors** can be expressed using a single character.<br>

```clojure
(rygb->hex "r").hex()  ;; => "#ff0000" ← red

(rygb->hex "y").hex()  ;; => "#ffff00" ← yellow

(rygb->hex "g").hex()  ;; => "#00ff00" ← green

(rygb->hex "b").hex()  ;; => "#0000ff" ← blue
```

<br>

**White, black, and neutral grays** can be expressed by limiting input to the `value` syntax.<br>

```clojure
(rygb->hex "v100")  ;; => "#ffffff" ← white

(rygb->hex "v0")  ;; => "#000000" ← black

(rygb->hex "v50")  ;; => "#808080" ← 50% gray
```

<br>

More examples with various colors:

```clojure
(rygb->hex "gb-s21-v83") ;; => "#a7d4d4" ← robin's egg blue

(rygb->hex "g2b5-s31-v47") ;; => "#536878" ← payne's gray

(rygb->hex "g21b1-s24-v88") ;; => "#abe0af" ← celadon

(rygb->hex "r1y3-s80-v96") ;; => "#f5c431" ← saffron

(rygb->hex "b9r4-s31") ;; => "#e1b0ff" ← mauve

(rygb->hex "yg") ;; => "rgba(128, 255, 0, 1.0)" ← chartreuse

(rygb->hex "y-s6") ;; => "#fffff0" ← ivory

(rygb->hex "v20") ;; => "#333333" ← jet]
```
<br>
<br>

## API ##
First, a quick overview by example. All example following are written as if a user `:refer`'d individual fns in ns. Otherwise, if you just require  `rygb.core :as rygb`, for example, then you would do `rygb/rygb->hex`, `rygb/rygb->rgb` etc:
```clojure
(rygb->hex "gb-s21-v83") ;; => "#a7d4d4" 

(rygb->hexa "gb-s21-v83") ;; => "#a7d4d4ff"

(rygb->int24 "gb-s21-v83") ;; => 10998739 

(rygb->rgb "gb-s21-v83") ;; => [ 0.6557, 0.83, 0.83 ] 

(rygb->rgb-bit "gb-s21-v83") ;; => [ 167, 212, 212 ] 

(rygb->rgb-css "gb-s21-v83") ;; => "rgb(167, 212, 212)" 

(rygb->rgba "gb-s21-v83") ;; => [ 0.6557, 0.83, 0.83, 1.0 ] 

(rygb->rgba-bit "gb-s21-v83") ;; => [ 167, 212, 212, 255 ] 

(rygb->rgba-css "gb-s21-v83") ;; => "rgba(167, 212, 212, 1.0)" 

(rygb->argb "gb-s21-v83") ;; => [ 1.0, 0.6557, 0.83, 0.83 ] 

(rygb->argb-bit "gb-s21-v83") ;; => [ 255, 167, 212, 212]

(rygb->map "gb-s21-v83") ;; => {:h {:g 1, :b 1}, :s 0.21, :v 0.83} 

(def my-color
 {:h {:g 1 :b 1}
  :s 0.21
  :v 0.83})

(rygb->string my-color) ;; => "gb-s21-v83" 
```

### Parser
The `rygb` parser will accept either a string or a map. When passing a map, the `:h` key represents the hue, and must be a map with either one or two keys. Unlike the string notation, the `:s`, `:v`, and `:a` values in the map must be decimal fractions `[0-1]`:
```clojure
// Equivalent to (rygb->hex "g2b5-s31-v47-a88")
(rygb->hex {:h {:g 2, :b 5}, :s 0.31, :v 0.47, :a 0.88})

// Equivalent to (rygb->hex "r")
(rygb->hex {:h {:r 1}})
```


### Serialization methods
`rygb->hex` is a perhaps the most immediately useful function when using RYGB notation for rapid prototyping.

```clojure
(rygb->hex "yg")  ;; => "#80ff00"
```

<br>

`rygb->hexa` will return a hex code with alpha-channel information (supported in some browsers).

```clojure
(rygb->hexa "yg-a88")  ;; => "#80ff00e0"
```

<br>

rgb, rgba, and argb tuples with values as decimal fractions `[0-1]`:
```clojure
(rygb->rgb "g2b3-s31-v47")  ;; => [0.3243, 0.44086, 0.47 ]
(rygb->rgba "g2b3-s31-v47")  ;; => [0.3243, 0.44086, 0.47, 1.0]
(rygb->argb "g2b3-s31-v47")  ;; => [1.0, 0.3243, 0.44086, 0.47]
```

<br>

Or with 8-bit unsigned integers`[0-255]`:
```clojure
(rygb->rgb-bit "g2b3-s31-v47"))  ;; => [83, 112, 120]
(rygb->rgba-bit "g2b3-s31-v47"))  ;; => [83, 112, 120, 255]
(rygb->argb-bit "g2b3-s31-v47"))  ;; => [255, 83, 112, 120]
```

<br>

A packed integer representing an rgb color:
```clojure
(rygb->int24 "g2b3-s31-v47")  ;; => 5402743
```

<br>

Turn a map into RYGB string notation:
```clojure
(rygb->string {:h {:g 2, :b 3}, :s 0.31, :v 0.47})  ;; => "g2b3-s31-v47"
```

<br>

Turn RYGB notation into a map:
```clojure
(rygb->map "g2b3-s31-v47") ;; => {:h {:g 2, :b 3}, :s 0.31, :v 0.47}
```

<br>

## Usage with other tools ##

`rygb` will work with almost any Clojure(Script)/JS library or framework whose constructors and/or color methods accept hex codes or rgb(a) css color strings. Most of them do.

In some scenarios, it may be desirable to make use of an rgb(a) tuple, or even a packed integer. For example, the .int24() method works nicely for [defining a color as recommended in three.js](https://threejs.org/docs/#api/en/math/Color):
```clojure
(def my-color (three/Color. (rygb->int24 "g2b3-s31-v47")))
```
<br>


Although color conversions and manipulations are outside the scope of this library, `rygb` can be used to supply parsable input to other packages that provide a vast array of such functionality. Popular, well-supported JS color libraries include  [color](https://github.com/Qix-/color), [one-color](https://github.com/One-com/one-color), [chroma.js](https://github.com/gka/chroma.js/), and [TinyColor](https://github.com/bgrins/TinyColor). Pure clj(s) libraries include [thi.ng/color](https://github.com/thi-ng/color), and [crumpets](https://github.com/weavejester/crumpets).

<br>

## License ##
Copyright © 2019 JC

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
