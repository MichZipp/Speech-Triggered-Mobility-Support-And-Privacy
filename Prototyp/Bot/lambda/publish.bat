del .\build\index.zip
7z a -r index.zip .\build\* .\node_modules 
aws lambda update-function-code --function-name MobilitySupportLambda --zip-file fileb://index.zip