# Machine Learning: Hands On for Developers and Technical Professionals - 2nd Edition

This is the repository for the code and data that is used within the book. 

## Shameless Ordering Plug

![A picture of the book, in real life!](https://dataissexy.files.wordpress.com/2020/03/20200226_145756.jpg?w=1058&h=1414)

[For more information on the book and formats available look at the product listing page on the Wiley website.](https://www.wiley.com/en-gb/Machine+Learning%3A+Hands+On+for+Developers+and+Technical+Professionals%2C+2nd+Edition-p-9781119642190)

## A few notes.
The core languages in the book are basically Java and Clojure. There's a chapter on R too, in some sections there's some bash scripting. 

In terms of machine learning frameworks, I settled for Weka and DeepLearning4J. While Weka may be old it's still a stable workhorse for a lot of algorithms, for a J48 Decision Tree or Support Vector Machine it's still my go to framework. DL4J replaces a lot of the neural network aspects in the book.

## Code Sections
In this repo I've divided out the code and the data into separate directories. Within the code directory the languages are also divided out and then into the respective chapter numbers.

## Build Instructions
Where possible each code chapter has its own Maven pom.xml file, for Java code, or a Leiningen project file for Clojure code. Build instructions are usually within the chapter text itself. 

## Support
Feel free to contact me. If I don't response immediately then please accept my apologies, I'm probably knee deep in a Kafka cluster somewhere. 




