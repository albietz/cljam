(ns cljam.sounds
  (:use overtone.live))

; sound definition (needs improvement...)
(definst foo [freq 440 on 0 mod-amp 4 mod-freq 8 pitch-bend 0]
         (* on (saw (+ (* mod-amp (sin-osc mod-freq))
                       (* freq (+ 1. pitch-bend))))))

; create map of sounds
(def foos (into {} (for [i (range 24)]
                     [(str "/8/push" (inc i)) (:id (foo (midi->hz (+ 48 i))))])))

(def server (osc-server 44100 "osc-clj"))

; OSC listener
(defn listener [msg]
  (let [{path :path, args :args} msg]
    (when-let [id (foos path)]   ; piano keys
      (ctl id :on (first args)))
    (when (= "/8/fader3" path)   ; pitch bend
      (ctl foo :pitch-bend (/ (- (first args) 0.5) 10)))
    (when (= "/8/fader2" path)   ; mod
      (ctl foo :mod-freq (* 15 (first args))))))

(osc-listen server listener :player)
