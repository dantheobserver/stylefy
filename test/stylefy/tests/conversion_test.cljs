(ns stylefy.tests.conversion-test
  (:require [cljs.test :as test :refer-macros [deftest is testing]]
            [stylefy.core :as stylefy]
            [stylefy.impl.styles :as styles]
            [stylefy.impl.conversion :as conversion]
            [clojure.string :as str]))

(def simple-style {:padding "25px"
                   :background-color "#BBBBBB"
                   :border "1px solid black"})

(deftest simple-style->css
  (is (= (conversion/style->css {:props simple-style :hash (styles/hash-style simple-style)}
                                {:pretty-print? false})
         "._stylefy_878532438{padding:25px;background-color:#BBBBBB;border:1px solid black}")))

(def clickable {:cursor :pointer})

(def autoprefix-style (merge {:border "1px solid black"
                              :border-radius "5px"
                              ::stylefy/vendors ["webkit" "moz" "o"]
                              ::stylefy/auto-prefix #{:border-radius}}
                             clickable))

(deftest autoprefixed-style->css
  (is (= (conversion/style->css {:props autoprefix-style :hash (styles/hash-style autoprefix-style)}
                                {:pretty-print? false})
         "._stylefy_-216657570{border:1px solid black;border-radius:5px;-webkit-border-radius:5px;-moz-border-radius:5px;-o-border-radius:5px;cursor:pointer}")))

(def style-mode {::stylefy/mode {:hover {:background-color "#AAAAAA"}}})
(def style-mode-double-colon {::stylefy/mode {"::-webkit-progress-bar" {:-webkit-appearance "none"}}})
(def style-incorrect-mode {::stylefy/mode {"-webkit-progress-bar" {:-webkit-appearance "none"}}})

(deftest mode-style->css
  (is (= (conversion/style->css {:props style-mode :hash (styles/hash-style style-mode)}
                                {:pretty-print? false})
         "._stylefy_-2110434399{}._stylefy_-2110434399:hover{background-color:#AAAAAA}")))

(deftest mode-style-double-colon->css
  (is (= (conversion/style->css {:props style-mode-double-colon :hash (styles/hash-style style-mode-double-colon)}
                                {:pretty-print? false})
         "._stylefy_-1391954833{}._stylefy_-1391954833::-webkit-progress-bar{-webkit-appearance:none}")))

(deftest incorrect-mode->css
  (try
    (conversion/style->css {:props style-incorrect-mode :hash (styles/hash-style style-incorrect-mode)}
                           {:pretty-print? false})
    (is false "Error was not thrown")
    (catch js/Error e
      (is true "Error was thrown as expected"))))

(def responsive-style {:background-color "red"
                       :border-radius "10px"
                       ::stylefy/vendors ["webkit" "moz" "o"]
                       ::stylefy/mode {:hover {:background-color "white"}}
                       ::stylefy/auto-prefix #{:border-radius}
                       ::stylefy/media {{:max-width "500px"}
                                        {:background-color "blue"
                                         :border-radius "5px"
                                         ::stylefy/mode {:hover {:background-color "grey"}}
                                         ::stylefy/vendors ["webkit" "moz" "o"]
                                         ::stylefy/auto-prefix #{:border-radius}}}})

(deftest responsive-style->css
  (is (= (conversion/style->css {:props responsive-style :hash (styles/hash-style responsive-style)}
                                {:pretty-print? false})
         "._stylefy_628215496{background-color:red;border-radius:10px;-webkit-border-radius:10px;-moz-border-radius:10px;-o-border-radius:10px}._stylefy_628215496:hover{background-color:white}@media(max-width:500px){._stylefy_628215496{background-color:blue;border-radius:5px;-webkit-border-radius:5px;-moz-border-radius:5px;-o-border-radius:5px}._stylefy_628215496:hover{background-color:grey}}")))


(def grid-layout-with-fallback {:display "flex"
                                :flex-direction "row"
                                :flex-wrap "wrap"
                                ::stylefy/mode {:hover {:background-color "white"}}
                                ::stylefy/media {{:max-width "500px"}
                                                 {:display "block"}}
                                ::stylefy/supports
                                {"display: grid"
                                 {:display "grid"
                                  :grid-template-columns "1fr 1fr 1fr"
                                  ;; Make CSS Grid responsive
                                  ::stylefy/media {{:max-width "500px"}
                                                   {:grid-template-columns "1fr"
                                                    ::stylefy/mode {:hover
                                                                    {:background-color "grey"}}}}}}})

(deftest supports->css
  (is (= (conversion/style->css {:props grid-layout-with-fallback
                                 :hash (styles/hash-style grid-layout-with-fallback)}
                                {:pretty-print? false})
         "._stylefy_-978876848{display:flex;flex-direction:row;flex-wrap:wrap}._stylefy_-978876848:hover{background-color:white}@media(max-width:500px){._stylefy_-978876848{display:block}}@supports (display: grid) {._stylefy_-978876848{display:grid;grid-template-columns:1fr 1fr 1fr}@media(max-width:500px){._stylefy_-978876848{grid-template-columns:1fr}._stylefy_-978876848:hover{background-color:grey}}}")))

(deftest custom-selector
  (is (= (conversion/style->css {:props {:color "red"} :custom-selector "code"}
                                {:pretty-print? false})
         "code{color:red}")))