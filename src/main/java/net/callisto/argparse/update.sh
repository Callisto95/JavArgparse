rm -- *.java
cp /run/media/callisto/Data-2TB/workspace/Java/Schkauti-Programming/src/main/java/schkauti/sudoku/args/* .
sed -i 's/schkauti\.sudoku\.args/net\.callisto\.argparse/g' -- *.java
