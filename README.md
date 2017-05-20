# stylefy

[![Clojars Project](https://img.shields.io/clojars/v/stylefy.svg)](https://clojars.org/stylefy)

An EXPERIMENTAL ClojureScript library for styling UI components with ease.

# Introduction

stylefy makes it possible to define UI component styles as Clojure data. Internally the defined styles are converted to CSS classes by using [Garden](https://github.com/noprompt/garden) and inserted into DOM. When styles are defined as Clojure data, they can be easily transformed with Clojure's powerful functions (like merge) and parametrised. Also, since the converted CSS is handled internally by the library, there is no need to worry about things like name conflicts etc.

stylefy has been tested to work with [Reagent](https://github.com/reagent-project/reagent). Other UI frameworks should work too if they use similar [Hiccup](https://github.com/weavejester/hiccup) syntax.

# Supported features

- Defining styles as Clojure data for any UI element
- Defining sub-styles (you can create a style for the root element of your UI component and then define substyles for the internal elements)

# Installation

Add the following line to your Leiningen project:

```clj
[stylefy "0.2.2"]
```

# Usage

```clj
(:require [stylefy.core :as stylefy])
```

First, call *stylefy/init* once when your application starts:

```clojure
(stylefy/init)
```

Create style as a normal Clojure map:

```clojure
(def generic-container {:padding "25px"
                        :background-color "#BBBBBB"
                        :border "1px solid black"})
```

To use it in a component, use the *use-style* function:

```clojure
(defn- button [text]
  [:div (use-style button-style)
    text])
```

Combine or parametrise styles however you like:

```clojure
(def primary-button (merge generic-button {:background-color "rgb(88, 121, 193)"}))
                                  
(defn button-style [background-color]
  (merge generic-button {:background-color background-color}))
```

Create styles in styles using sub-styles (useful, if you want to define styles for the root component and it's sub elements in a single map):

```clojure
(def container-style (merge
                       generic-container
                       {::stylefy/sub-styles {:list {:margin-top "1em"}}}))

(defn list-in-container []
  [:div (use-style styles/container-style)
   [:ul (use-sub-style styles/container-style :list)
    [:li "List element 1"]
    [:li "List element 2"]
    [:li "List element 3"]]])
```

More examples available here: https://github.com/Jarzka/stylefy/tree/master/examples/src/stylefy/main

# Changelog

Here: https://github.com/Jarzka/stylefy/releases
