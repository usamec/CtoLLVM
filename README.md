CtoLLVM
=======

V terajsom stave kompilator cita subor test.c a vyraba subor test.ll. Prikladom skompilovatelneho
suboru je example.c.
Kompiluje sa prikazom:
ant run

Ten pocas behu aj vo formate .dot vypise sparsovany strom (ak ho chceme pouzit, odporucam 
kompilovat prikazom ant run -emacs). Ten sa da copy&pastnut do stranky:
http://graphviz-dev.appspot.com/

LLVM sa pusta prikazmi: llvm-as -f test.ll && lli test.bc

Podporovane features:
- scitanie (ostatne vyrazy treba dorobit, je to copy&paste scitanie a usamcovi sa nechce)
- ||, &&
- \<, \>, \<=, \>=
- \*, & (dereferencuj a adresuj)
- assignment (iba =, ostatne su copy&paste)
- vsetky druhy konstant (v zakladnych tvaroch)
- if/else
- while
- return, break, continue
- deklaracie funkcii so scalar argumentami (zakladne argumenty + pointre na ne)
- definicie funkcii so scalar argumentami
- volanie funkcii
- polia

Easy TODO:
- aritmetika (okrem - pre pointre)
- do while, for

Hard TODO:
- - pre pointre
- vsetky mozne dalsie typy (struktury, typedefs, ...)
- poriadne argumenty funkcii (povolit [], argumenty bez nazvy v deklaracii)

Ked uz nebude co robit TODO:
- vyhodnocovanie konstatnych vyrazov
- checkovanie hranic konstant
- konstanta co rozlisuje medzi int a long long
