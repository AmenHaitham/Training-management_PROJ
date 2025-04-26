// DOM Elements
const addTrainingBtn = document.getElementById('addTrainingBtn');
const trainingModal = document.getElementById('trainingModal');
const closeButton = document.querySelector('.close-button');
const cancelBtn = document.getElementById('cancelBtn');
const trainingForm = document.getElementById('trainingForm');
const trainingTableBody = document.getElementById('trainingTableBody');

// Training data (this would come from a database in a real app)
let trainings = [
  { id: 1, title: "Leadership Skills", description: "Leadership training for managers.", status: "active", createdBy: "John Doe" },
  { id: 2, title: "Project Management", description: "Project management for teams.", status: "upcoming", createdBy: "Jane Smith" },
  { id: 3, title: "Project Management", description: "Project management for teams.", status: "upcoming", createdBy: "Jane Smith" },
  { id: 4, title: "Project Management", description: "Project management for teams.", status: "upcoming", createdBy: "Jane Smith" },
  { id: 5, title: "Project Management", description: "Project management for teams.", status: "upcoming", createdBy: "Jane Smith" },
  { id: 6, title: "Project Management", description: "Project management for teams.", status: "upcoming", createdBy: "Jane Smith" },
  { id: 7, title: "Project Management", description: "Project management for teams.", status: "upcoming", createdBy: "Jane Smith" },
  { id: 8, title: "Project Management", description: "Project management for teams.", status: "upcoming", createdBy: "Jane Smith" }

];

let nextId = 3; // For generating new IDs
let editId = null; // To track if we are editing an existing training

// Event Listeners
addTrainingBtn.addEventListener('click', openModal);
closeButton.addEventListener('click', closeModal);
cancelBtn.addEventListener('click', closeModal);
trainingForm.addEventListener('submit', handleFormSubmit);

// Initialize the table
renderTrainings();

// Functions
function openModal() {
  trainingForm.reset();
  trainingModal.style.display = 'flex';
  document.body.style.overflow = 'hidden'; // Prevent scrolling
}

function closeModal() {
  trainingModal.style.display = 'none';
  document.body.style.overflow = 'auto'; // Re-enable scrolling
  trainingForm.reset();
  editId = null; // Reset editId when closing
  document.querySelector('.modal-header h2').textContent = "Add New Training";
  document.querySelector('.form-actions button[type="submit"]').textContent = "Add Training";
}

function handleFormSubmit(e) {
  e.preventDefault();

  const trainingData = {
    title: document.getElementById('trainingTitle').value.trim(),
    description: document.getElementById('trainingDescription').value.trim(),
    status: document.getElementById('trainingStatus').value,
    createdBy: document.getElementById('trainingCreatedBy').value.trim()
  };

  if (editId) {
    // Edit existing
    const index = trainings.findIndex(t => t.id === editId);
    trainings[index] = { id: editId, ...trainingData };
    alert('Training updated successfully!');
  } else {
    // Add new
    trainings.push({ id: nextId++, ...trainingData });
    alert('Training added successfully!');
  }

  renderTrainings();
  closeModal();
}

function renderTrainings() {
  trainingTableBody.innerHTML = '';

  trainings.forEach(training => {
    const row = document.createElement('tr');
    row.innerHTML = `
      <td>${training.id}</td>
      <td>${training.title}</td>
      <td>${training.description}</td>
      <td>${training.status}</td>
      <td>${training.createdBy}</td>
      <td class="actions">
        <button class="edit" onclick="editTraining(${training.id})">Edit</button>
        <button class="delete" onclick="deleteTraining(${training.id})">Delete</button>
      </td>
    `;
    trainingTableBody.appendChild(row);
  });
}

function editTraining(id) {
  const training = trainings.find(t => t.id === id);
  if (training) {
    document.getElementById('trainingTitle').value = training.title;
    document.getElementById('trainingDescription').value = training.description;
    document.getElementById('trainingStatus').value = training.status;
    document.getElementById('trainingCreatedBy').value = training.createdBy;

    editId = training.id;

    document.querySelector('.modal-header h2').textContent = "Edit Training";
    document.querySelector('.form-actions button[type="submit"]').textContent = "Save Changes";

    trainingModal.style.display = 'flex';
    document.body.style.overflow = 'hidden';
  }
}

function deleteTraining(id) {
  if (confirm('Are you sure you want to delete this training?')) {
    trainings = trainings.filter(training => training.id !== id);
    renderTrainings();
    alert('Training deleted successfully.');
  }
}

// Close modal if clicking outside
window.addEventListener('click', (e) => {
  if (e.target === trainingModal) {
    closeModal();
  }
});
