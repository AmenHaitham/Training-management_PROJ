<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Training Management</title>
  <link rel="stylesheet" href="CSS/Trainings.css"> <!-- Link to the external CSS -->
  <style>
    /* Inline style for Back Button if needed */
    .back-button {
      background-color: #c0392b; /* Green */
      border: none;
      color: white;
      padding: 8px 16px;
      text-align: center;
      text-decoration: none;
      display: inline-block;
      font-size: 14px;
      margin: 10px 0;
      cursor: pointer;
      border-radius: 4px;
    }
    .top-bar {
      display: flex;
      justify-content: flex-start;
      align-items: center;
      margin-bottom: 10px;
    }
  </style>
</head>
<body>

  <div class="container">
    <div class="top-bar">
      <button id="backBtn" class="back-button">Back</button>
    </div>

    <h1>Training Management</h1>

    <button id="addTrainingBtn" class="button">Add Training</button>

    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>Title</th>
          <th>Description</th>
          <th>Status</th>
          <th>Created By</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody id="trainingTableBody">
        <!-- Trainings will be dynamically injected here -->
      </tbody>
    </table>
  </div>

  <!-- Add Training Modal -->
  <div id="trainingModal" class="modal">
    <div class="modal-content">
      <div class="modal-header">
        <h2>Add New Training</h2>
        <button class="close-button">&times;</button>
      </div>
      <form id="trainingForm">
        <div class="form-group">
          <label for="trainingTitle">Title</label>
          <input type="text" id="trainingTitle" required>
        </div>
        <div class="form-group">
          <label for="trainingDescription">Description</label>
          <input type="text" id="trainingDescription" required>
        </div>
        <div class="form-group">
          <label for="trainingStatus">Status</label>
          <select id="trainingStatus" required>
            <option value="active">Active</option>
            <option value="completed">Completed</option>
            <option value="upcoming">Upcoming</option>
          </select>
        </div>
        <div class="form-group">
          <label for="trainingCreatedBy">Created By</label>
          <input type="text" id="trainingCreatedBy" required>
        </div>
        <div class="form-actions">
          <button type="button" class="button secondary" id="cancelBtn">Cancel</button>
          <button type="submit" class="button">Add Training</button>
        </div>
      </form>
    </div>
  </div>

  <script src="Js/Trainings.js"></script> <!-- Link to the external JS -->

  <script>
    // Back button functionality
    document.getElementById('backBtn').addEventListener('click', function() {
      window.location.href = 'Dashboard.jsp'; // Adjust the path if needed
    });
  </script>
</body>
</html>
