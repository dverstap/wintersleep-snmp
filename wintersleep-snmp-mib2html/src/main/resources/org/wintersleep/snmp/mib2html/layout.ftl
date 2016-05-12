[#macro layout title]
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>

    <title>${title}</title>

    <meta http-equiv="Content-Type" content="text/xhtml; charset=UTF-8"/>
    <link rel="stylesheet" href="bootstrap.min.css"/>
</head>
<body>

<nav class="navbar navbar-inverse">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="index.html">SNMP Mib</a>
        </div>
        <ul class="nav navbar-nav">
            <li ${(title == 'Modules') ? then('class="active"', "")}><a href="index.html">Modules</a></li>
            <li ${(title == 'Tables') ? then('class="active"', "")}><a href="tables.html">Tables</a></li>
            <li ${(title == 'Columns') ? then('class="active"', "")}><a href="columns.html">Columns</a></li>
            <li ${(title == 'Scalars') ? then('class="active"', "")}><a href="scalars.html">Scalars</a></li>
            <li ${(title == 'Variables') ? then('class="active"', "")}><a href="variables.html">Variables</a></li>
            <li ${(title == 'Traps') ? then('class="active"', "")}><a href="traps.html">Traps</a></li>
            <li ${(title == 'Notifications') ? then('class="active"', "")}><a
                    href="notifications.html">Notifications</a>
            </li>
            <li ${(title == 'Types') ? then('class="active"', "")}><a href="types.html">Types</a></li>
            <li ${(title == 'OIDs') ? then('class="active"', "")}><a href="oids.html">OIDs</a></li>
        </ul>
    </div>
</nav>

<div class="container-fluid">
    [#nested]
</div>

[/#macro]
