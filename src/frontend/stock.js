const API_BASE_URL = "http://localhost:8081/api";

// Get the stock id from the URL, e.g. stock.html?id=3
const params = new URLSearchParams(window.location.search);
const stockId = params.get("id");

async function loadStockDetail() {
    const loadingEl = document.getElementById("loading");
    const errorEl = document.getElementById("error");
    const detailEl = document.getElementById("stock-detail");

    if (!stockId) {
        loadingEl.style.display = "none";
        errorEl.style.display = "block";
        errorEl.textContent = "No stock id provided in the URL.";
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/stocks/${stockId}`);

        if (!response.ok) {
            throw new Error(`Server responded with status ${response.status}`);
        }

        const stock = await response.json();

        loadingEl.style.display = "none";
        detailEl.style.display = "block";

        document.getElementById("detail-symbol").textContent = stock.symbol;
        document.getElementById("detail-sector").textContent = stock.sector;
        document.getElementById("detail-company").textContent = stock.companyName;
        document.getElementById("detail-exchange").textContent = stock.exchange;

        document.getElementById("detail-current-price").textContent =
            stock.currentPrice != null ? `$${stock.currentPrice.toFixed(2)}` : "N/A";
        document.getElementById("detail-previous-price").textContent =
            stock.previousPrice != null ? `$${stock.previousPrice.toFixed(2)}` : "N/A";
        document.getElementById("detail-volume").textContent =
            stock.volume != null ? stock.volume.toLocaleString() : "N/A";

    } catch (err) {
        loadingEl.style.display = "none";
        errorEl.style.display = "block";
        errorEl.textContent = `Failed to load stock: ${err.message}`;
        console.error(err);
    }
}

async function getAiSummary() {
    const btn = document.getElementById("summary-btn");
    const summaryLoadingEl = document.getElementById("summary-loading");
    const summaryErrorEl = document.getElementById("summary-error");
    const summaryTextEl = document.getElementById("summary-text");

    btn.disabled = true;
    summaryErrorEl.style.display = "none";
    summaryTextEl.textContent = "";
    summaryLoadingEl.style.display = "block";

    try {
        const response = await fetch(`${API_BASE_URL}/stocks/${stockId}/summary`);

        if (!response.ok) {
            throw new Error(`Server responded with status ${response.status}`);
        }

        const data = await response.json();
        summaryTextEl.textContent = data.summary;

    } catch (err) {
        summaryErrorEl.style.display = "block";
        summaryErrorEl.textContent = `Could not generate summary: ${err.message}`;
        console.error(err);
    } finally {
        summaryLoadingEl.style.display = "none";
        btn.disabled = false;
    }
}

document.getElementById("summary-btn").addEventListener("click", getAiSummary);

loadStockDetail();