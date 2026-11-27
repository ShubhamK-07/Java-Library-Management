#!/bin/sh
# Run from the project folder: sh run_tests.sh
javac src/*.java || exit 1
failed=0
for input in tests/*.txt; do
    case "$input" in *_expected.txt)
    continue ;; esac
    expected="tests/$(basename "$input" .txt)_expected.txt"
    if java -cp src Main < "$input" | diff --strip-trailing-cr "$expected" -; then
        echo "PASS $input"
    else
        echo "FAIL $input"
        failed=1
    fi
done
rm -f src/*.class
exit "$failed"