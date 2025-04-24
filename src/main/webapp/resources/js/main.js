document.addEventListener('DOMContentLoaded', function () {
    // Kiểm tra hợp lệ cho form đăng nhập
    const loginForm = document.querySelector('form');
    if (loginForm) {
        loginForm.addEventListener('submit', function (e) {
            const username = document.getElementById('username').value.trim();
            if (!username) {
                e.preventDefault();
                alert('Vui lòng nhập tên người dùng');
            }
        });
    }

    // Khởi tạo kết nối WebSocket nếu đang ở trang chat
    const chatContainer = document.querySelector('.chat-container');
    if (chatContainer) {
        initializeWebSocket();

        // Thêm trạng thái typing animation
        const messageInput = document.getElementById('message-input');
        if (messageInput) {
            messageInput.addEventListener('focus', function () {
                this.placeholder = "Đang nhập tin nhắn...";
            });

            messageInput.addEventListener('blur', function () {
                this.placeholder = "Nhập tin nhắn của bạn...";
            });
        }
    }
});

// Chức năng WebSocket
function initializeWebSocket() {
    // Lấy tên người dùng và phòng từ trang
    const username = document.getElementById('current-user').dataset.username;
    const room = document.getElementById('current-room').dataset.room;

    // Tạo kết nối WebSocket
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const wsUrl = `${protocol}//${window.location.host}${window.location.pathname.replace(/\/[^\/]*$/, '')}/chat-socket/${room}`;

    const socket = new WebSocket(wsUrl);

    // Hiển thị lịch sử tin nhắn từ localStorage khi tải trang
    const chatHistory = loadChatHistory(room);
    chatHistory.forEach(message => {
        displayMessage(message, false); // false nghĩa là không lưu vào localStorage
    });

    // Hiển thị thông báo phân tách giữa tin nhắn cũ và tin nhắn mới
    if (chatHistory.length > 0) {
        displaySystemMessage('--- Tin nhắn cũ ---', false);
    }
    // Khi kết nối được mở
    socket.addEventListener('open', function (event) {
        console.log('Đã kết nối tới máy chủ WebSocket');
        // Gửi thông báo người dùng đã tham gia
        sendMessage({
            type: 'JOIN',
            username: username,
            room: room,
            content: username + ' đã tham gia phòng chat.'
        });

        // Hiển thị thông báo kết nối
        showConnectionToast('Đã kết nối đến phòng chat!');
    });

    // Lắng nghe tin nhắn từ máy chủ
    socket.addEventListener('message', function (event) {
        const message = JSON.parse(event.data);
        displayMessage(message);

        // Thông báo âm thanh khi có tin nhắn mới (chỉ khi không phải tin nhắn của mình)
        if (message.type === 'CHAT' && message.username !== username) {
            playMessageSound();
        }
    });

    // Khi kết nối bị đóng
    socket.addEventListener('close', function (event) {
        console.log('Đã ngắt kết nối với máy chủ WebSocket');
        displaySystemMessage('Mất kết nối. Vui lòng tải lại trang để kết nối lại.');
        showConnectionToast('Mất kết nối!', true);
    });

    // Khi có lỗi kết nối
    socket.addEventListener('error', function (event) {
        console.error('Lỗi WebSocket:', event);
        displaySystemMessage('Lỗi khi kết nối tới máy chủ chat.');
        showConnectionToast('Lỗi kết nối!', true);
    });

    // Gửi tin nhắn khi gửi form chat
    const chatForm = document.getElementById('chat-form');
    const messageInput = document.getElementById('message-input');

    if (chatForm) {
        chatForm.addEventListener('submit', function (e) {
            e.preventDefault();
            const content = messageInput.value.trim();

            if (content) {
                sendMessage({
                    type: 'CHAT',
                    username: username,
                    room: room,
                    content: content
                });

                messageInput.value = '';
                messageInput.focus(); // Giữ focus tại ô input sau khi gửi
            }
        });
    }

    // Hàm gửi tin nhắn qua WebSocket
    function sendMessage(message) {
        if (socket.readyState === WebSocket.OPEN) {
            socket.send(JSON.stringify(message));
        }
    }

    // Sự kiện trước khi đóng cửa sổ để thông báo người dùng rời phòng
    window.addEventListener('beforeunload', function () {
        sendMessage({
            type: 'LEAVE',
            username: username,
            room: room,
            content: username + ' đã rời khỏi phòng chat'
        });
    });
}

// Hiển thị thông báo kết nối
function showConnectionToast(message, isError = false) {
    // Kiểm tra xem đã có toast chưa
    let toast = document.querySelector('.connection-toast');

    // Nếu chưa có, tạo mới
    if (!toast) {
        toast = document.createElement('div');
        toast.classList.add('connection-toast');
        document.body.appendChild(toast);
    }

    // Thiết lập nội dung và style
    toast.textContent = message;
    toast.className = 'connection-toast'; // Reset classes

    if (isError) {
        toast.classList.add('error');
    } else {
        toast.classList.add('success');
    }

    // Hiển thị toast
    toast.classList.add('show');

    // Tự động ẩn sau 3 giây
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// Phát âm thanh khi có tin nhắn
function playMessageSound() {
    // Tạo đối tượng âm thanh
    const audio = new Audio('data:audio/mp3;base64,SUQzBAAAAAAAI1RTU0UAAAAPAAADTGF2ZjU4Ljc2LjEwMAAAAAAAAAAAAAAA/+M4wAAAAAAAAAAAAEluZm8AAAAPAAAAAwAAAbsAVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVQ==');
    audio.play();
}

// Hiển thị tin nhắn nhận được trong khung chat
function displayMessage(message) {
    const chatMessages = document.querySelector('.chat-messages');
    const currentUsername = document.getElementById('current-user').dataset.username;

    const messageElement = document.createElement('div');
    messageElement.classList.add('message');

    // Xác định thời gian
    const time = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

    // Xác định tin nhắn này là của hệ thống, người dùng hiện tại hay người khác
    if (message.type === 'SYSTEM') {
        messageElement.classList.add('message-system');
        messageElement.innerHTML = `
            <div class="message-text">${formatMessageContent(message.content)}</div>
        `;
    } else if (message.type === 'JOIN' || message.type === 'LEAVE') {
        messageElement.classList.add('message-event');
        messageElement.innerHTML = `
            <div class="message-text">
                <i class="fas ${message.type === 'JOIN' ? 'fa-sign-in-alt' : 'fa-sign-out-alt'}"></i> 
                ${formatMessageContent(message.content)}
            </div>
        `;
    } else {
        const isOutgoing = message.username === currentUsername;
        messageElement.classList.add(isOutgoing ? 'message-outgoing' : 'message-incoming');

        // Thêm hiệu ứng xuất hiện
        messageElement.classList.add('message-animated');

        messageElement.innerHTML = `
            <div class="message-info">
                <span class="message-username">${message.username}</span>
                <span class="message-time"> [${time}]</span>
            </div>
            <div class="message-text">${formatMessageContent(message.content)}</div>
        `;
    }

    chatMessages.appendChild(messageElement);

    // Cuộn xuống cuối khung chat
    chatMessages.scrollTop = chatMessages.scrollHeight;

    // Lưu tin nhắn vào localStorage nếu cần
    if (saveToStorage) {
        // Đảm bảo message có timestamp
        if (!message.timestamp) {
            message.timestamp = new Date().toISOString();
        }
        saveMessageToHistory(message, currentRoom);
    }
}
// Hàm lưu tin nhắn vào localStorage
function saveMessageToHistory(message, room) {
    // Lấy lịch sử hiện tại hoặc tạo một mảng mới nếu chưa có
    let chatHistory = JSON.parse(localStorage.getItem('chatHistory')) || {};

    // Tạo mảng cho phòng nếu chưa tồn tại
    if (!chatHistory[room]) {
        chatHistory[room] = [];
    }

    // Thêm tin nhắn vào lịch sử của phòng
    chatHistory[room].push(message);

    // Giới hạn số lượng tin nhắn lưu trữ (giữ 50 tin nhắn gần nhất)
    const MAX_MESSAGES = 50;
    if (chatHistory[room].length > MAX_MESSAGES) {
        chatHistory[room] = chatHistory[room].slice(-MAX_MESSAGES);
    }

    // Lưu trở lại vào localStorage
    localStorage.setItem('chatHistory', JSON.stringify(chatHistory));
}

// Hàm tải lịch sử tin nhắn từ localStorage
function loadChatHistory(room) {
    const chatHistory = JSON.parse(localStorage.getItem('chatHistory')) || {};
    return chatHistory[room] || [];
}

// Hàm xóa lịch sử của một phòng
function clearRoomHistory(room) {
    let chatHistory = JSON.parse(localStorage.getItem('chatHistory')) || {};
    if (chatHistory[room]) {
        delete chatHistory[room];
        localStorage.setItem('chatHistory', JSON.stringify(chatHistory));
    }
}
// Format nội dung tin nhắn với emoji và links
function formatMessageContent(content) {
    // Chuyển URLs thành links có thể nhấp vào
    const urlRegex = /(https?:\/\/[^\s]+)/g;
    content = content.replace(urlRegex, '<a href="$1" target="_blank">$1</a>');

    // Chuyển đổi emojis text thành biểu tượng cảm xúc thực
    content = content
        .replace(/:smile:/g, '😊')
        .replace(/:sad:/g, '😢')
        .replace(/:lol:/g, '😂')
        .replace(/:heart:/g, '❤️')
        .replace(/:thumbsup:/g, '👍')
        .replace(/:thumbsdown:/g, '👎');

    return content;
}
// Hàm xóa lịch sử phòng hiện tại
function clearCurrentRoomHistory() {
    const room = document.getElementById('current-room').dataset.room;
    if (confirm(`Bạn có chắc muốn xóa lịch sử chat của phòng "${room}"?`)) {
        clearRoomHistory(room);
        // Xóa các tin nhắn trên giao diện
        const chatMessages = document.querySelector('.chat-messages');
        chatMessages.innerHTML = '';
        // Hiển thị thông báo
        displaySystemMessage('Đã xóa lịch sử chat.');
    }
}

// Hiển thị tin nhắn hệ thống
function displaySystemMessage(message) {
    displayMessage({
        type: 'SYSTEM',
        content: message
    });
}

// Chức năng chuyển phòng chat
function switchRoom(roomId) {
    // Chuyển hướng tới phòng đã chọn
    window.location.href = `${window.location.pathname.replace(/\/[^\/]*$/, '')}/chat?room=${roomId}`;
}

// CSS cho toast thông báo kết nối
document.addEventListener('DOMContentLoaded', function () {
    // Tạo style cho connection-toast
    const style = document.createElement('style');
    style.textContent = `
        .connection-toast {
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 12px 20px;
            border-radius: 8px;
            color: white;
            font-weight: 500;
            z-index: 1000;
            opacity: 0;
            transform: translateY(-20px);
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
        }
        
        .connection-toast.success {
            background: linear-gradient(45deg, #28a745, #20c997);
        }
        
        .connection-toast.error {
            background: linear-gradient(45deg, #dc3545, #f56565);
        }
        
        .connection-toast.show {
            opacity: 1;
            transform: translateY(0);
        }
        
        .message-system {
            text-align: center;
            background: rgba(0, 0, 0, 0.05);
            color: #666;
            font-style: italic;
            padding: 8px 15px;
            border-radius: 20px;
            margin: 10px 0;
            font-size: 0.9rem;
            align-self: center;
            max-width: 80%;
        }
        
        .message-event {
            text-align: center;
            background: rgba(37, 117, 252, 0.1);
            color: #2575fc;
            padding: 8px 15px;
            border-radius: 20px;
            margin: 10px 0;
            font-size: 0.9rem;
            align-self: center;
            max-width: 80%;
        }
        
        .message-animated {
            animation: messageAppear 0.3s ease-out;
        }
        
        @keyframes messageAppear {
            from {
                opacity: 0;
                transform: translateY(10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
    `;
    document.head.appendChild(style);
});