#!/bin/bash

# Add your API key from openweathermaps.org
API_KEY=xxxxxxx

# Note the escaped & other wise the interpreter will attempt to run a background task.

curl -o londonweather.json https://api.openweathermap.org/data/2.5/weather?q=London\&APPID=${API_KEY}
