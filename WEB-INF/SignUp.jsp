<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TMS Sign Up</title>
    <link rel="stylesheet" href="CSS/signUp.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
</head>

<body>

<div id="container">
    <div id="leftContainer">
        <h1>Training Management System</h1>
        <div class="icon-container">
            <i class="fas fa-clipboard-list checklist icon"></i>

        </div>
        <h2>Welcome!</h2>
        <p>Create your account</p>
    </div>

    <div id="rightContainer">

        <h3>Sing Up</h3>

        <div id="Fields">

            <div id="columnOne">
                <span>First Name</span>
                <input type="text" class="Feild">

                <span>Birth Date</span>
                <input type="date" class="Feild">


                <span>Email</span>
                <input type="email" class="Feild">
             </div>


            <div id="columnTwo">
                <span>Last Name</span>
                <input type="text" class="Feild">

                <span>Phone Number</span>
                <input type="text" class="Feild">


                <span>Password</span>
                <input type="password" class="Feild">
            </div>
      </div>



            <button>Sign Up</button>
            <div id="signUp">
                <p>have an account !</p>
                <a href="Login.jsp">Sign In</a>
            </div>
    </div>           
</div>
    </body>
</html>