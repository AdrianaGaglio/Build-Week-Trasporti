// const test = () => {
//   fetch("http://localhost:8000/tratte", {
//     method: "POST",
//     headers: {
//       "Content-Type": "application/json"
//     },
//     body: JSON.stringify({ partenza: "A", capolinea: "B", durata: "00:05:00", percorrenze: [] })
//   })
//     .then((response) => response.text())
//     .then((data) => console.log(data));
// };

// test();

// const deleteTratta = () => {
//   fetch("http://localhost:8000/tratte/203", {
//     method: "DELETE",
//   })
//     .then((response) => {
//       if (response.ok) {
//         console.log("Tratta eliminata con successo");
//       } else {
//         console.error("Errore durante l'eliminazione:", response.status, response.statusText);
//       }
//     })
//     .catch((error) => console.error("Errore di rete:", error));
// };

// deleteTratta();

// const putTratta = () => {
//     fetch("http://localhost:8000/tratte/204", {
//       method: "PUT",
//       headers: {
//         "Content-Type": "application/json"
//       },
//       body: JSON.stringify({
//         id: 204,
//         partenza: "Aaaaa",
//         capolinea: "sadadaB",
//         durata: "00:10:00"
//       })
//     })
//       .then((response) => {
//         if (response.ok) {
//           console.log("Tratta aggiornata con successo");
//         } else {
//           console.error("Errore durante l'aggiornamento:", response.status, response.statusText);
//         }
//       })
//       .catch((error) => console.error("Errore di rete:", error));
//   };

//   putTratta();
