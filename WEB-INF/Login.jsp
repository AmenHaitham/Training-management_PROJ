<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TMS Login</title>
    <link rel="stylesheet" href="CSS/login.css">
    <!-- Include Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
</head>
<body>
    <div id="container">
        <div id="leftContainer">
            <h1>Training Management System</h1>
            <div class="icon-container">
                <i class="fas fa-clipboard-list checklist icon"></i>
            </div>
            <h2>Welcome Back!</h2>
            <p>Sign in to your account</p>
        </div>

        <div id="rightContainer">
            <form action="login.jsp" method="post">
                <h3>Sign In</h3>

                <div class="inputGroup">
                    
            <span>Email</span>
            <input type="text" class="Feild">

                </div>

                <div class="inputGroup">
                    
            <span>Password</span>
            <input type="password" class="Feild">

                </div>

                <div id="optionsContainer">
                    <div id="rememberMe">
                        <input type="checkbox" name="rememberMe" id="rememberMeCheck">
                        <label for="rememberMeCheck">Remember me</label>
                    </div>
                
                    
                        <a href="ResetPassword.jsp">Forgot password?</a>
                    
                
                </div>

                <button type="submit" onclick="location.herf='Dashboard.jsp'">Sign In</button>

                <div id="signUp">
                    <p>Don't have an account?</p>
                    <a href="SignUp.jsp">Sign Up</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
