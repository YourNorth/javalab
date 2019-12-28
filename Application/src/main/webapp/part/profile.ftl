<#macro first>
    <link rel="stylesheet" href="http://bootstraptema.ru/plugins/2015/bootstrap4/bootstrap.min.css"/>
    <link rel="stylesheet" href="http://bootstraptema.ru/plugins/font-awesome/4-4-0/font-awesome.min.css"/>

    <style>
        body {
            background: url(http://bootstraptema.ru/images/bg/bg-1.png)
        }
    </style>

    <div class="container" style="margin-top: 100px">
    <div id="main">


    <div class="row" id="real-estates-detail">
    <div class="col-lg-4 col-md-4 col-xs-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                <header class="panel-title">
                    <div class="text-center">
                        <strong>Пользователь сайта</strong>
                    </div>
                </header>
            </div>
            <div class="panel-body">
                <div class="text-center" id="author">
                    <img src="${user.img}" width="200" height="200">
                    <#nested>
                    <h3>${user.username!"Imya"}</h3>
                    </p>
                </div>
            </div>
        </div>
    </div>
</#macro>