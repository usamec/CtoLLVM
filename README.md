CtoLLVM
=======

V terajsom stave kompilator cita subor test.c a vyraba subor test.ll.
Kompiluje sa prikazom:
ant run

Ten pocas behu aj vo formate .dot vypise sparsovany strom (ak ho chceme pouzit, odporucam 
kompilovat prikazom ant run -emacs). Ten sa da copy&pastnut do stranky:
http://graphviz-dev.appspot.com/

Podporovane features:
- scitanie (ostatne vyrazy treba dorobit, je to copy&paste scitanie a usamcovi sa nechce)
- ||, &&
- \<, \>, \<=, \>=
- \*, & (dereferencuj a adresuj)
- assignment
- vsetky druhy konstant (v zakladnych tvaroch)
- if/else
- while
- return, break, continue
- deklaracie funkcii so scalar argumentami (zakladne argumenty + pointre na ne)
- definicie funkcii so scalar argumentami
- volanie funkcii
- polia
