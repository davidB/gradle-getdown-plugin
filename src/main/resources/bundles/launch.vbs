Dim shell, fso, appRoot, javaCmd, jarPath, i
Set shell = CreateObject("WScript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")
appRoot    = shell.CurrentDirectory
'Set appRoot = fso.GetParentFolderName(WScript.ScriptFullName)
javaCmd = appRoot & "\jre\bin\java"
If Not fso.FileExists(javaCmd) Then
   javaCmd = "java"
End If
jarPath = appRoot & "\app\getdown.jar"
For Each i In fso.GetFolder(appRoot & "\app").Files
  'If (InStr(1, i.Name, "getdown") = 1) Then
  If (InStr(1, i.Name, "getdown-") = 1) And (InStr(1, i.Name, ".jar") = (Len(i.Name) - 3)) Then
    jarPath = i.Path
  End If
Next
'Wscript.Echo javaCmd & " -jar """ &  jarPath & """ .\app"
shell.Run javaCmd & " -jar """ &  jarPath & """ .\app", 0, False
