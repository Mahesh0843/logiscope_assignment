<!DOCTYPE html>
<html>
<head>
    <title>Update User</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <h2>Update User</h2>
    <form action="update" method="post">
        <input type="email" name="email" placeholder="Email" required>
         <input type="password" name="password" placeholder="Password" required>
        <input type="text" name="newFirstName" placeholder="New First Name" required>
        <input type="text" name="newLastName" placeholder="New Last Name" required>
        <button type="submit">Update</button>
    </form>
</body>
</html>
