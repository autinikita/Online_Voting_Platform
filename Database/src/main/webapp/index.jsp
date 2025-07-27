<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>JSP Button Example</title>
    <style>
        button:disabled {
            background-color: #ccc;
            cursor: not-allowed;
        }
    </style>
</head>
<body>
    <h3>Click a Button</h3>
    <div id="buttons">
        <button onclick="handleClick(this, 'Button 1')">Button 1</button>
        <button onclick="handleClick(this, 'Button 2')">Button 2</button>
        <button onclick="handleClick(this, 'Button 3')">Button 3</button>
        <button onclick="handleClick(this, 'Button 4')">Button 4</button>
    </div>
    <p id="clicked-data">Clicked Button Data: None</p>

    <!-- Pass the clicked button data back to the server -->
    <form id="dataForm" method="post" action="process.jsp" style="display:none;">
        <input type="hidden" name="buttonData" id="buttonData">
    </form>

    <script>
        function handleClick(button, buttonData) {
            // Disable all buttons
            const allButtons = document.querySelectorAll("button");
            allButtons.forEach(btn => btn.disabled = true);

            // Update the clicked button data
            document.getElementById("clicked-data").innerText = Clicked Button Data: ${buttonData};

            // Pass the data to the hidden form and submit
            document.getElementById("buttonData").value = buttonData;
            document.getElementById("dataForm").submit();
        }
    </script>
</body>
</html>