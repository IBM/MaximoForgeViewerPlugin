set SOURCE=.
set TARGET=.
set PACKAGENAME=Maximo Forge Viewer Plug-in
set BINPATH=ForgeViewerPlugin

xcopy /c /y /s /i "%SOURCE%\doc" "%TARGET%\%PACKAGENAME%"
xcopy /c /y /s /i "%SOURCE%\Sample Model" "%TARGET%\%PACKAGENAME%\Sample Model"
copy /y "%SOURCE%\LICENSE" "%TARGET%\%PACKAGENAME%"

xcopy /c /y /s /i "%SOURCE%\bin\psdi\app" "%TARGET%\%PACKAGENAME%\%BINPATH%\applications\maximo\businessobjects\classes\psdi\app"
xcopy /c /y /s /i "%SOURCE%\bin\psdi\webclient\beans" "%TARGET%\%PACKAGENAME%\%BINPATH%\applications\maximo\maximouiweb\webmodule\WEB-INF\classes\psdi\webclient\beans"
xcopy /c /y /s /i "%SOURCE%\bin\psdi\bimlmv" "%TARGET%\%PACKAGENAME%\%BINPATH%\tools\maximo\classes\psdi\bimlmv"

xcopy /c /y /s /i "%SOURCE%\applications\maximo\maximouiweb\webmodule\webclient\BIMField" "%TARGET%\%PACKAGENAME%\%BINPATH%\applications\maximo\maximouiweb\webmodule\webclient\BIMField"
xcopy /c /y /s /i "%SOURCE%\applications\maximo\maximouiweb\webmodule\webclient\components\bimlmv" "%TARGET%\%PACKAGENAME%\%BINPATH%\applications\maximo\maximouiweb\webmodule\webclient\components\bimlmv"
xcopy /c /y /s /i "%SOURCE%\applications\maximo\maximouiweb\webmodule\webclient\skins" "%TARGET%\%PACKAGENAME%\%BINPATH%\applications\maximo\maximouiweb\webmodule\webclient\skins"
mkdir "%TARGET%/%PACKAGENAME%\%BINPATH%\applications\maximo\maximouiweb\webmodule\webclient\javascript"
copy /y "%SOURCE%\applications\maximo\maximouiweb\webmodule\webclient\javascript\Forge.js" "%TARGET%\%PACKAGENAME%\%BINPATH%\applications\maximo\maximouiweb\webmodule\webclient\javascript"
copy /y "%SOURCE%\applications\maximo\maximouiweb\webmodule\webclient\javascript\LMV*.js" "%TARGET%\%PACKAGENAME%\%BINPATH%\applications\maximo\maximouiweb\webmodule\webclient\javascript"
copy /y "%SOURCE%\applications\maximo\maximouiweb\webmodule\webclient\javascript\gunzip.min.js" "%TARGET%\%PACKAGENAME%\%BINPATH%\applications\maximo\maximouiweb\webmodule\webclient\javascript"

mkdir "%TARGET%\%PACKAGENAME%\%BINPATH%\applications\maximo\properties\product"
copy /y "%SOURCE%\applications\maximo\properties\product\bimlmv.xml" "%TARGET%\%PACKAGENAME%\%BINPATH%\applications\maximo\properties\product"

xcopy /c /y /s /i "%SOURCE%\resources" "%TARGET%\%PACKAGENAME%\%BINPATH%\resources"

xcopy /c /y /s /i "%SOURCE%\tools\maximo\en\bimlmv" "%TARGET%\%PACKAGENAME%\%BINPATH%\tools\maximo\en\bimlmv"

set "currentdir=%cd%"
xcopy /c /y /s /i "%SOURCE%\IBM-Forge-Viewer-CLI" "%TARGET%\%PACKAGENAME%\IBM-Forge-Viewer-CLI"
cd "%TARGET%\%PACKAGENAME%\%BINPATH%\applications\maximo\businessobjects\classes"
jar -cf IBM-Forge-1.0.jar psdi\app\bim\viewer\dataapi psdi\app\bim\viewer\dataapi\cli
rmdir /S /Q psdi\app\bim\viewer\dataapi\cli
cd %currentdir%
move "%TARGET%\%PACKAGENAME%\%BINPATH%\applications\maximo\businessobjects\classes\IBM-Forge-1.0.jar"  "%TARGET%\%PACKAGENAME%\IBM-Forge-Viewer-CLI"