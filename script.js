document.addEventListener('DOMContentLoaded', function() {
    var chessboard = document.getElementById('chessboard');
    for (var row = 0; row < 8; row++) {
        for (var col = 0; col < 8; col++) {
            var square = document.createElement('div');
            square.className = 'chess-square';
            // Alternate colors
            if ((row + col) % 2 == 0) {
                square.style.backgroundColor = '#789c54'; // Make sure the color name is valid
            } else {
                square.style.backgroundColor = '#f0eccc'; // Make sure the color name is valid
            }
            chessboard.appendChild(square);
        }
    }
});
