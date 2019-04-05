CALL lein clean
REM the min-option seems to produce invalid call in (defn image...)
REM CALL lein cljsbuild once min
CALL lein cljsbuild once
firebase deploy