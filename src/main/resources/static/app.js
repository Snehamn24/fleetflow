let currentTripId = null;

async function bookTrip() {

    const customerId = document.getElementById("customerId").value;
    const pickup = document.getElementById("pickup").value;
    const destination = document.getElementById("destination").value;
    const vehicleType = document.getElementById("vehicleType").value;
    const distanceKm = document.getElementById("distanceKm").value;
    const idempotencyKey =
        document.getElementById("idempotencyKey").value;

    const requestBody = {
        customerId: Number(customerId),
        pickup: pickup,
        destination: destination,
        vehicleType: vehicleType,
        distanceKm: Number(distanceKm)
    };

    try {

        const response = await fetch("/api/trips", {
            method: "POST",

            headers: {
                "Content-Type": "application/json",
                "Idempotency-Key": idempotencyKey
            },

            body: JSON.stringify(requestBody)
        });

        const data = await response.json();

        if (!response.ok) {
            document.getElementById("tripResult").innerHTML =
                "Error: " + (data.message || data.error);

            return;
        }

        currentTripId = data.id;

        document.getElementById("tripResult").innerHTML = `
            <strong>Trip ID:</strong> ${data.id}<br>
            <strong>Status:</strong> ${data.status}<br>
            <strong>Route:</strong> ${data.pickup} → ${data.destination}<br>
            <strong>Distance:</strong> ${data.distanceKm} km<br>
            <strong>Fare:</strong> ₹${data.fareAmount}<br>
            <strong>Driver:</strong> ${data.driver.name}<br>
            <strong>Vehicle:</strong> ${data.vehicle.registrationNumber}
        `;

    } catch (error) {

        document.getElementById("tripResult").innerHTML =
            "Unable to connect to FleetFlow backend.";
    }
}