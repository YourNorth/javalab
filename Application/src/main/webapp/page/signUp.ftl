<#import "../part/common.ftl" as c>
<@c.login title="Tittle">
    <main class="page contact-page" style="margin-top: 100px;">
        <section class="portfolio-block contact" >
            <div class="container" >
                <div class="heading">
                    <h2>Регистрация</h2>
                </div>
                <form method="post">
                    <div class="form-group"><label for="subject">Имя</label><input class="form-control item" type="text" id="subject" name="username" aria-describedby="emailHelp" placeholder="Введите имя"></div>
                    <div class="form-group"><label for="password">Пароль</label><input class="form-control item" type="password" id="password" name="password" placeholder="Введите пароль"></div>
                    <div class="form-group"><button class="btn btn-primary btn-block btn-lg" type="submit" style="background-color: rgb(140,90,64);border-color: rgb(140,90,64);">Войти</button></div>
                </form>
            </div>
        </section>
    </main>
</@c.login>
