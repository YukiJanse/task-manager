/* app.js — Notion-ish frontend
   Anpassa apiBase om backend körs på annan host/port
*/
const apiBase = "/tasks";

// --- DOM ---
const colNotStarted = document.getElementById("col-notstarted");
const colInProgress = document.getElementById("col-inprogress");
const colDone = document.getElementById("col-done");

const countNot = document.getElementById("count-notstarted");
const countProg = document.getElementById("count-inprogress");
const countDone = document.getElementById("count-done");

const modal = document.getElementById("modal");
const openNew = document.getElementById("openNew");
const closeModal = document.getElementById("closeModal");
const cancelBtn = document.getElementById("cancelBtn");
const deleteBtn = document.getElementById("deleteBtn");

const form = document.getElementById("taskForm");
const inputId = document.getElementById("taskId");
const inputTitle = document.getElementById("title");
const inputDesc = document.getElementById("description");
const inputStart = document.getElementById("startDatetime");
const inputEnd = document.getElementById("endDatetime");
const selectStatus = document.getElementById("status");

const themeToggle = document.getElementById("themeToggle");

// --- events ---
openNew.addEventListener("click", () => openForm());
closeModal.addEventListener("click", closeForm);
cancelBtn.addEventListener("click", closeForm);
themeToggle.addEventListener("click", () => document.body.classList.toggle("dark"));

// form submit
form.addEventListener("submit", async (e) => {
    e.preventDefault();
    await saveTask();
});

// delete
deleteBtn.addEventListener("click", async () => {
    const id = inputId.value;
    if (!id) return closeForm();
    if (!confirm("Vill du radera uppgiften?")) return;
    await fetch(`${apiBase}/${id}`, {method: "DELETE"});
    closeForm();
    loadTasks();
});

// initial load
loadTasks();

// --- FUNCTIONS ---
async function loadTasks() {
    try {
        const res = await fetch(apiBase);
        const tasks = await res.json();

        // clear columns
        [colNotStarted, colInProgress, colDone].forEach(c => c.innerHTML = "");

        let counts = {NOT_STARTED: 0, IN_PROGRESS: 0, DONE: 0};

        tasks.forEach(t => {
            renderCard(t);
            counts[t.status] = (counts[t.status] || 0) + 1;
        });

        countNot.textContent = counts.NOT_STARTED || 0;
        countProg.textContent = counts.IN_PROGRESS || 0;
        countDone.textContent = counts.DONE || 0;
    } catch (err) {
        console.error("Could not load tasks", err);
    }
}

function renderCard(task) {
    const card = document.createElement("div");
    card.className = "card";
    card.draggable = true;
    card.dataset.id = task.id;

    const title = document.createElement("h4");
    title.textContent = task.title || "(no title)";

    const desc = document.createElement("p");
    desc.textContent = task.description || "";

    const meta = document.createElement("div");
    meta.className = "meta";

    if (task.startDatetime) meta.appendChild(elSmall(formatShort(task.startDatetime)));
    if (task.endDatetime) meta.appendChild(elSmall("→ " + formatShort(task.endDatetime)));
    meta.appendChild(elSmall(task.status));

    const actions = document.createElement("div");
    actions.className = "card-actions";

    const editBtn = document.createElement("button");
    editBtn.className = "btn";
    editBtn.textContent = "Edit";
    editBtn.addEventListener("click", (e) => {
        e.stopPropagation();
        openForm(task);
    });

    actions.appendChild(editBtn);

    card.appendChild(title);
    card.appendChild(desc);
    card.appendChild(meta);
    card.appendChild(actions);

    // drag handlers
    card.addEventListener("dragstart", (e) => {
        e.dataTransfer.setData("text/plain", task.id);
        card.classList.add("dragging");
    });
    card.addEventListener("dragend", () => card.classList.remove("dragging"));

    // append to correct column
    if (task.status === "NOT_STARTED") colNotStarted.appendChild(card);
    else if (task.status === "IN_PROGRESS") colInProgress.appendChild(card);
    else if (task.status === "DONE") colDone.appendChild(card);
}

// small meta helper
function elSmall(text) {
    const s = document.createElement("small");
    s.textContent = text;
    return s;
}

function formatShort(dt) {
    // dt expected as "yyyy-MM-ddTHH:mm:ss" or ISO — return "yyyy-mm-dd HH:MM"
    if (!dt) return "";
    const d = new Date(dt);
    if (isNaN(d)) {
        // fallback: cut string
        return dt.replace('T', ' ').slice(0, 16);
    }
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const hh = String(d.getHours()).padStart(2, '0');
    const mm = String(d.getMinutes()).padStart(2, '0');
    return `${y}-${m}-${day} ${hh}:${mm}`;
}

// --- drag/drop helpers ---
function allowDrop(ev) {
    ev.preventDefault();
    ev.currentTarget.classList.add("drag-over");
}

window.addEventListener("dragleave", (e) => {
    const target = e.target;
    if (target && target.classList) target.classList.remove("drag-over");
});

function drop(ev, newStatus) {
    ev.preventDefault();
    const id = ev.dataTransfer.getData("text/plain");
    // remove drag visuals
    document.querySelectorAll(".col-body").forEach(c => c.classList.remove("drag-over"));
    if (!id) return;
    updateStatus(id, newStatus);
}

// update status via PATCH endpoint
async function updateStatus(id, status) {
    try {
        await fetch(`${apiBase}/${id}/update-status`, {
            method: "PATCH",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(status) // send e.g. "IN_PROGRESS"
        });
        loadTasks();
    } catch (err) {
        console.error("Status update failed", err);
    }
}

// --- FORM / modal ---
function openForm(task) {
    if (!task) {
        // new
        inputId.value = "";
        inputTitle.value = "";
        inputDesc.value = "";
        inputStart.value = "";
        inputEnd.value = "";
        selectStatus.value = "NOT_STARTED";
        document.getElementById("modal-title").textContent = "Ny uppgift";
        deleteBtn.style.display = "none";
    } else {
        inputId.value = task.id;
        inputTitle.value = task.title || "";
        inputDesc.value = task.description || "";
        inputStart.value = toLocalInput(task.startDatetime);
        inputEnd.value = toLocalInput(task.endDatetime);
        selectStatus.value = task.status || "NOT_STARTED";
        document.getElementById("modal-title").textContent = "Redigera uppgift";
        deleteBtn.style.display = "inline-block";
    }
    modal.classList.remove("hidden");
}

function closeForm() {
    modal.classList.add("hidden");
}

// convert backend datetime -> datetime-local value (yyyy-mm-ddTHH:MM)
function toLocalInput(dt) {
    if (!dt) return "";
    // dt might be "yyyy-MM-ddTHH:mm:ss"
    const s = dt.slice(0, 16); // "yyyy-MM-ddTHH:mm"
    return s;
}

// save (create or update)
async function saveTask() {
    const id = inputId.value;
    const payload = {
        title: inputTitle.value,
        description: inputDesc.value,
        // append seconds to match backend pattern
        startDatetime: inputStart.value ? inputStart.value + ":00" : null,
        endDatetime: inputEnd.value ? inputEnd.value + ":00" : null,
        status: selectStatus.value
    };

    try {
        if (id) {
            // update
            await fetch(`${apiBase}/${id}`, {
                method: "PUT",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(payload)
            });
        } else {
            // create
            await fetch(apiBase, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(payload)
            });
        }
        closeForm();
        loadTasks();
    } catch (err) {
        console.error("Save failed", err);
    }
}
