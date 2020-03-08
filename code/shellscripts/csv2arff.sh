#!/bin/bash

java -cp /path/to/weka.jar weka.core.converters.CSVLoader kmeansdata.csv > kmeansdata.arff
