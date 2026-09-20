const API_BASE_URL = "http://localhost:8081/api";

async function loadStocks() {
    const loadingEl = document.getElementById("loading");
    const errorEl = document.getElementById("error");
    const listEl = document.getElementById("stock-list");

    try {
        const response = await fetch(`${API_BASE_URL}/stocks`);

        if (!response.ok) {
            throw new Error(`Server responded with status ${response.status}`);
        }

        const stocks = await response.json();

        loadingEl.style.display = "none";

        if (stocks.length === 0) {
            listEl.innerHTML = "<p>No stocks found.</p>";
            return;
        }

        stocks.forEach(stock => {
            const card = document.createElement("div");
            card.className = "stock-card";
            card.innerHTML = `
                <div class="stock-symbol">${stock.symbol}</div>
                <div class="stock-name">${stock.companyName}</div>
                <div class="stock-sector">${stock.sector}</div>
            `;
            card.addEventListener("click", () => {
                window.location.href = `stock.html?id=${stock.id}`;
            });
            listEl.appendChild(card);
        });

    } catch (err) {
        loadingEl.style.display = "none";
        errorEl.style.display = "block";
        errorEl.textContent = `Failed to load stocks: ${err.message}. Is the backend running on ${API_BASE_URL}?`;
        console.error(err);
    }
}

loadStocks();