mkdir -p lib/ && (cd lib && curl 'http://jade.tilab.com/dl.php?file=JADE-all-4.4.0.zip' > JADE-all-4.4.0.zip && jar xvf JADE-all-4.4.0.zip && rm -f JADE-all-4.4.0.zip && for F in *.zip; do jar xvf $F; done && rm -f *.zip)
echo "Add these to classpath:"
echo "$(pwd)/lib/jade/lib/jade.jar"
echo "$(pwd)/lib/jade/lib/commons-codec/commons-codec-1.3.jar`"
