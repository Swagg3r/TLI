#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys
from math import *


def trace(function, xmin, xmax, nstep, output):
    #output.write("x, %s\n" % function)
    function = eval("lambda x:" + function)
    coord = []

    longueur = abs(xmin)+xmax

    step = 1.*(xmax-xmin)/nstep

    for i in range(nstep+1):
        x = xmin + i*step
        try:
            y = function(x)
        except:
            continue
        #output.write("%s, %s\n" % (x, y))
        coord.append((x,y))

    #Ecriture du repere dans un fichier postscript
    output.write(   "%!\n\
/cm { 28.3464567 mul } def\n\
/repere {\n\
    /Arial findfont\n\
    .5 cm scalefont\n\
    setfont\n\
    newpath\n\
    0 5 cm moveto\n\
    "+str(longueur)+" cm 5 cm lineto\n\
    (x) show\n\
    "+str(abs(xmin))+" cm 0 moveto\n\
    "+str(abs(xmin))+" cm 10 cm lineto\n\
    (y) show\n\
    stroke\n\
} def\n\
\n\
"+str(abs(xmin))+" cm 5 cm translate\n\
repere\n")
    output.write(str(abs(xmin))+" cm 5 cm translate\n")
    output.write("newpath\n")
    output.write("%s cm %s cm moveto\n" % (coord[0][0],coord[0][1]))
    output.write("%s cm %s cm moveto\n" % (coord[1][0],coord[1][1]))
    for i in range(0,len(coord)):
        if i==0:
            output.write("%s cm %s cm moveto\n" % (coord[i][0],coord[i][1]))
        else:
            output.write("%s cm %s cm lineto\n" % (coord[i][0], coord[i][1]))
    output.write("stroke\n")
    output.write("showpage\n")


def main(argv=None):
    if argv is None:
        argv = sys.argv
    
    import getopt
    try:
        options, argv = getopt.getopt(argv[1:], "o:", ["output=", "xmin=", "xmax=", "nstep="])
    except getopt.GetoptError as message:
        sys.stderr.write("%s\n" % message)
        sys.stderr.write("Usage: python trace.py <arguments> \"fonction\"\n");
        sys.stderr.write("Options: [-o output_file] [--xmin number1] [--xmax number2] [--nstep number3]\n");
        sys.exit(1)
    
    if len(argv) != 1:
        sys.stderr.write("Usage: python trace.py <arguments> \"fonction\"\n");
        sys.stderr.write("Options: [-o output_file] [--xmin number1] [--xmax number2] [--nstep number3] \n");
        sys.exit(1)

    function = argv[0]
    output = sys.stdout
    xmin, xmax = 0., 1.
    nstep = 10
    
    for option, value in options:
        if option in ["-o", "--output"]:
            output = file(value, "w")
        elif option == "--xmin":
            xmin = float(value)
        elif option == "--xmax":
            xmax = float(value)
        elif option == "--nstep":
            nstep = int(value)
        else:
            assert False, "unhandled option"

    #Si nstep vaut 0, division par zero impossible.
    if nstep == 0:
        sys.stdout.write("Division par 0 impossible\n")
        sys.exit(-1)

    #Si la valeur de xmin est superieur a celle de xmax, il y a un probleme.
    if xmin >= xmax:
        sys.stdout.write("Erreur : xmin est superieur a xmax\n")
        sys.exit(-1)
    
    trace(function, xmin, xmax, nstep, output)


if __name__ == "__main__":
    sys.exit(main())
