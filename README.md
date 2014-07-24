noncircular
===========

Contains code to generate non-circular gears. It was hacked together as a "while the baby sleeps" project and development ceased as soon as it reached the point I could use it to generate gears to cut. The main class is NonCircApplet. It specifies a hard coded path the file containing an svg of the gear shape you want to cut. The shape must be closed and there is a bug which means it only works if you draw it one way, (anticlockwise I vaugely recal).The output is a pdf - I then use inkscape to trace the path and export to dxf.  

It uses Processing www.processing.org as a library. 

Code is offered as is to do with as you will. 
