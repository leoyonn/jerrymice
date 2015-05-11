<html>
<head><title>JerryMice Demo</title></head>
<body>

<h1>Welcome to the JerryMice Demo page!</h1>

<hr>
<h2>Demo for parameters in url path </h2>
<a href='/demo/hello/jerry'>hello/jerry</a><br/>

<hr>
<h2>Demo for parameters in query string </h2>
<a href='/demo/hello?name=mice'>hello?name=mice</a><br/>

<hr>
<h2>Demo for regex in url path, like /hello/{<\d+>id}/{any} </h2>
<a href='/demo/hello/2481178/wtf-is-jerrymice'>hello/2481178/wtf-is-jerrymice</a><br/>

<hr>
<h2>Demo for jsonable in query string </h2>
<a href='/demo/hellojson?json={"name"="jerryjson","value"=248117811781178,"desc"="should not serialize!"}'>
    hellojson?json={"name"="jerryjson","value"=248117811781178,"desc"="should not serialize!"}'
</a><br/>

<hr>
<h2>Demo for post, async, context, model and jsp page!</h2>

<form action="/demo/async2" method="post" enctype="text/plain">
    <p>value1: <input type="text" name="value1"/></p>

    <p>value2: <input type="text" name="value2"/></p>

    <p>what else in the form</p>
    <input type="submit" value="Test context and async and jsp!"/>
</form>

<hr>
<h2>Demo for async timeout!</h2>
<a href='/demo/async/test-async-timeout-param'>test-async-timeout</a><br/>

<hr>
<h2>Demo for upload an text-file!</h2>

<form action="/demo/file" method="post" enctype="multipart/form-data">
    <p>Please choose a small text file for your sake: <input type="file" name="file"/></p>
    <input type="submit" value="Do upload!"/>
</form>

<hr>
<h2>Demo for filters</h2>
<a href='/demo/filter/fakelogin?user=mice&pass=jerry'>fakelogin/user=mice&pass=jerry</a><br/>
<a href='/demo/filter/fakelogout'>fakelogout</a><br/>
<a href='/demo/filter/auth'>check auth</a><br/>
<a href='/demo/filter/auth-dont-log'>check auth-dont-log</a><br/>
<h2>${info}</h2>
<hr>

</body>
</html>
