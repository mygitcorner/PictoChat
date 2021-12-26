let nickNameField = document.getElementById('nickname-field');
let nickNameBtn = document.getElementById('nickname-btn');

let drawBtn = document.getElementById('draw-btn');
let nickNameLabel = document.getElementById('nickname-label');
let inputField = document.getElementById('input-box');
let msgForm = document.getElementById('msg-form');

let msgSection = document.getElementById('msg-section');
let msgpanel = document.getElementById('msg-panel');
let msgTable = document.getElementById('msg-table');
let userListTable = document.getElementById('user-list-table');
let contentTd = document.getElementById('content');

let canvas = document.createElement('canvas');
canvas.width = 200;
canvas.height = 100;

let sendCanvasBtn = document.createElement('input');
sendCanvasBtn.type = 'button';
sendCanvasBtn.value = 'Send painting';

let clearCanvasBtn = document.createElement('input');
clearCanvasBtn.type = 'button';
clearCanvasBtn.value = 'Clear';

let closeCanvasBtn = document.createElement('input');
closeCanvasBtn.type = 'button';
closeCanvasBtn.value = 'Close';

let drawingDiv = document.createElement('div');
drawingDiv.id = 'drawingDiv';
drawingDiv.appendChild(canvas);
drawingDiv.style.position = "absolute"
drawingDiv.style.right = "20px";
drawingDiv.style.bottom = "0px";
drawingDiv.style.display = "none";
drawingDiv.appendChild(document.createElement('br'));
drawingDiv.appendChild(sendCanvasBtn);
drawingDiv.appendChild(closeCanvasBtn);
drawingDiv.appendChild(clearCanvasBtn);
contentTd.appendChild(drawingDiv);

let isDrawing = false;
let leftMouseIsDown = false;

window.addEventListener('pointerdown', function(event) {
    if (event.button == 0) {
        leftMouseIsDown = true;
    }
});
window.addEventListener('pointerup', function(event) {
    if (event.button == 0) {
        leftMouseIsDown = false;
    }
});

drawBtn.onclick = function(event) {
    event.preventDefault();
    drawingDiv.style.display = 'block';
}

sendCanvasBtn.onclick = function(event) {
    canvas.toBlob(function(blob) {
        blob.arrayBuffer().then(buffer => sendImage(buffer));
    });

    clearCanvas();
    drawingDiv.style.display = 'none';
}

clearCanvasBtn.onclick = function(event) {
    clearCanvas();
}

closeCanvasBtn.onclick = function(event) {
    drawingDiv.style.display = 'none';
}

function clearCanvas() {
    let ctx = canvas.getContext("2d");
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.beginPath();
}

function getCanvasMousePos(event) {
    let rect = canvas.getBoundingClientRect(),
        scaleX = canvas.width / rect.width,
        scaleY = canvas.height / rect.height;
  
    return {
      x: (event.clientX - rect.left) * scaleX,
      y: (event.clientY - rect.top) * scaleY
    }
  }

canvas.addEventListener("pointerdown", function (event) {
    event.preventDefault();
    let ctx = canvas.getContext("2d");
    let mousePos = getCanvasMousePos(event);
    ctx.moveTo(mousePos.x, mousePos.y);
    isDrawing = true;
});

window.addEventListener("pointerup", function() {
    isDrawing = false;
});

window.addEventListener("pointermove", function(event) {
    if (isDrawing) {
        let ctx = canvas.getContext("2d");
        ctx.strokeStyle = "#FFFFFF"
        let mousePos = getCanvasMousePos(event);
        ctx.lineTo(mousePos.x, mousePos.y);
        ctx.stroke();
    }
});

msgSection.style.display = 'none';

let user = new Object();
user.nickName = null;
user.channel = { name: null, userList: [] };

let inputList = [""];
let inputListIndex = 0;

let autoCompleteList = [];
let autoCompleteListIndex = 0;
let autoCompleteStartPos = 0;

function timeAsString() {
    let now = new Date();
    let hours = now.getHours();
    let minutes = now.getMinutes();
    let seconds = now.getSeconds();

    return (hours < 10 ? "0" : "") + hours + ":"
        + (minutes < 10 ? "0" : "") + minutes + ":"
        + (seconds < 10 ? "0" : "") + seconds;
}

function getSpanNode(str, className) {
    let node = document.createElement('span');
    node.textContent = str;
    if (className != null) {
        node.classList.add(className);
    }
    return node;
}

function getNickNameNode(aUser) {
    if (aUser.roles.includes('o')) {
        return getSpanNode(aUser.nickName, 'oper');
    } else {
        return getSpanNode(aUser.nickName, null);
    }
}

function addEntry(headingNode, contentNode) {
    let row = msgTable.insertRow(msgTable.rows.length - 1);
    row.classList.add('msg');

    headingNode.classList.add('msg-head')
    let col1 = row.insertCell(0);
    col1.append(getSpanNode("[" + timeAsString() + "]", "time"));
    col1.append(headingNode);

    let col2 = row.insertCell(1);
    col2.append(contentNode);

    msgpanel.scrollTop = msgpanel.scrollHeight - msgpanel.clientHeight;
}

function addJoinInfo(nickName, channel) {
    let message = (nickName == user.nickName)
            ? "You joined " + channel
            : nickName + " has joined ";
    addEntry(getSpanNode("*", 'join-info'), getSpanNode(message, 'join-info'));
}

function addQuitInfo(nickName, channel) {
    let message = (nickName == user.nickName)
            ? "You have quit " + channel
            : nickName + " has quit";
    addEntry(getSpanNode("*", 'quit-info'), getSpanNode(message, 'quit-info'));
}

function addMessage(nickName, message) {
    addEntry(getNickNameNode(getUser(nickName)), document.createTextNode(message));
}

function addInfo(message) {
    addEntry(getSpanNode('*', 'info'), getSpanNode(message, 'info'));
}

function addErrorInfo(message) {
    addEntry(getSpanNode('*', 'error'), getSpanNode(message, 'error'));
}

function addChannelListInfo(channels) {
    let node = getSpanNode("", 'info');

    let fragment = new DocumentFragment();
    fragment.append("Channels in this server:");

    let list = document.createElement('ul');
    for (let channel of channels) {
        let listItem = document.createElement('li');
        listItem.innerText = channel;
        list.append(listItem);
    }

    fragment.append(list);
    node.append(fragment);

    addEntry(getSpanNode("*", 'info'), node);
}

function addOperatorInfo(nickName) {
    let message = (nickName == user.nickName)
            ? "You are now an operator"
            : nickName + " is now an operator";
    addEntry(getSpanNode("*", 'join-info'), getSpanNode(message, 'join-info'));
}

function addKickInfo(operName, targetName, channel) {
    let message;

    if (user.nickName == operName) {
        message = "You have kicked " + targetName;
    } else if (user.nickName == targetName) {
        message = "You have been kicked from " + channel;
    } else {
        message = operName + " has kicked " + targetName;
    }

    addEntry(getSpanNode("*", 'quit-info'), getSpanNode(message, 'quit-info'));
}

function addImage(nickName, image) {
    addEntry(getNickNameNode(getUser(nickName)), image);
}

function log(str) {
    console.log(str);
    /*
    let elem = document.createElement('p');
    elem.innerText = str;
    logPanel.append(elem);
    logPanel.scrollTop = logPanel.scrollHeight - logPanel.clientHeight;
    */
}

function updateUserList() {
    while (userListTable.rows.length > 1) {
        userListTable.deleteRow(1);
    }

    for (let aUser of user.channel.userList) {
        let row = userListTable.insertRow();
        let cell = row.insertCell();
        cell.append(getNickNameNode(aUser));
    }
}

function getUser(nickName) {
    return user.channel.userList.find(aUser => aUser.nickName == nickName);
}

let ws = null;

function connect(host) {
    if (ws != null) {
        ws.close();
    }

    ws = new WebSocket('ws://' + host + '/PictoChat/chats');

    ws.onopen = function() {
        addInfo('Connected to ' + host);
    }
    
    ws.onmessage = function(event) {
        if (typeof event.data === 'string') {
            handleStringMessage(event.data);
        } else if (event.data instanceof Blob) {
            handleBinaryMessage(event.data);
        }
    };
    
    ws.onclose = function (event) {
        user.nickName = null;
        user.channel.name = null;
        user.channel.userList = [];
        updateUserList();

        addErrorInfo('Server connection closed: ' + event.code);
    }
}

function handleBinaryMessage(blob) {
    blob.arrayBuffer().then(function(buffer) {
        let dataView = new DataView(buffer);
        let pos = 0;
        let newLineCodePoint = "\n".codePointAt(0);
        while (dataView.getUint8(pos) != newLineCodePoint) {
            ++pos;
        }

        let textBuffer = buffer.slice(0, pos);
        let textDecoder = new TextDecoder();
        let paramLine = textDecoder.decode(new Uint8Array(textBuffer));
        let nickName = paramLine.split(" ")[0];

        let imageBlob = blob.slice(pos + 1, blob.size, 'image/png');
        let url = URL.createObjectURL(imageBlob);
        let img = new Image();

        img.onload = function() {
            URL.revokeObjectURL(url);
            addImage(nickName, img);
        }

        img.src = url;
    });
}

function handleStringMessage(message) {
    let tokens = message.split(" ");

    switch (tokens[0]) {
        case "NICK":
            {
                let oldNickName = tokens[1];
                let newNickName = tokens[2];
                let aUser = getUser(oldNickName);
                if (aUser != null) {
                    aUser.nickName = newNickName;
                    updateUserList();
                }
            }
            break;
        case "JOIN":
            {
                let channel = tokens[1];
                let nickName = tokens[2];
                let userInfo = tokens.slice(3);

                user.channel.userList = [];
                let currUser = null;
                for (let token of userInfo) {
                    if (token.startsWith("-")) {
                        currUser.roles.push(token.charAt(1));
                    } else {
                        currUser = { nickName:token, roles:[] };
                        user.channel.userList.push(currUser);
                    }
                }

                if (nickName == user.nickName) {
                    let oldchannel = user.channel.name;
                    user.channel.name = channel;
                    if (oldchannel != null) {
                        handleCommand('\part', oldchannel);
                    }
                }
                addJoinInfo(nickName, channel);
                updateUserList();
            }
            break;
        case "PART":
            {
                let channel = tokens[1];
                let nickName = tokens[2];
                if (nickName == user.nickName) {
                    if (channel == user.channel.name) {
                        user.channel.userList = [];
                        user.channel.name = null;
                        addQuitInfo(nickName, channel);
                    }
                } else {
                    user.channel.userList = user.channel.userList.filter(u => u.nickName != nickName);
                    addQuitInfo(nickName, channel);
                }
                
                updateUserList();
            }
            break;
        case "PRIVMSG":
            {
                let nickName = tokens[2];
                let message = tokens.slice(3).join(" ");
                addMessage(nickName, message)
            }
            break;
        case "LIST":
            let channels = tokens.slice(1);
            addChannelListInfo(channels);
            break;
        case "OPER":
            {
                let nickName = tokens[2];
                getUser(nickName).roles.push('o');
                addOperatorInfo(nickName);
                updateUserList();
            }
            break;
        case "KICK":
            {
                let operName = tokens[1];
                let channel = tokens[2];
                let nickName = tokens[3];
                if (nickName == user.nickName) {
                    user.channel.name = null;
                    user.channel.userList = null;
                } else {
                    user.channel.userList = user.channel.userList.filter(u => u.nickName != nickName);
                }
                addKickInfo(operName, nickName, channel);
                updateUserList();
            }
            break;
        case "ERR":
            {
                let errorMessage = tokens.slice(1).join(" ");
                addErrorInfo(errorMessage);
            }
            break;
        default:
            break;
    }
}

function handleCommand(command, paramStr) {
    switch (command) {
        case 'nick':
            {
                let nickName = paramStr;
                ws.send("NICK " + nickName);
                setNickName(nickName);
            }
            break;
        case 'list':
            ws.send('LIST');
            break;
        case 'join':
            {
                let channel = paramStr;
                ws.send('JOIN ' + channel);
            }
            break;
        case 'part':
            {
                let channel = paramStr;
                ws.send('PART ' + channel);
            }
            break;
        case 'oper':
            {
                let password = paramStr
                ws.send("OPER " + user.channel.name + " " + password);
            }
            break;
        case 'kick':
            {
                let targetName = paramStr
                ws.send("KICK " + user.channel.name + " " + targetName);
            }
            break;
        case 'connect':
            {
                let host = paramStr;
                connect(host);
            }
            break;
        case 'img':
            {
                let url = paramStr;
                fetch('http://localhost:8080/QuickServletApp/' + url)
                    .then(response => response.arrayBuffer())
                    .then(buffer => sendImage(buffer));
            }
            break;
        default:
            break;
    }
}

function sendImage(buffer) {
    let blob = new Blob([user.channel.name, "\n", buffer]);
    ws.send(blob);
}

msgForm.addEventListener('submit', function(event) {
    event.preventDefault();

    let request = inputField.value;
    if (request.startsWith("\\")) {
        let tokens = request.slice(1).split(" ", 2);
        handleCommand(tokens[0], tokens[1]);
    } else {
        ws.send("PRIVMSG " + user.channel.name + " " + request);
    }

    inputList[0] = inputField.value;
    inputField.value = "";
    inputList.unshift("");
    inputListIndex = 0;
});

inputField.addEventListener('keydown', function(event) {
    if (event.key == "ArrowUp" && inputListIndex < inputList.length - 1) {
        event.preventDefault();
        inputListIndex++;
        inputField.value = inputList[inputListIndex];
    }

    if (event.key == "ArrowDown" && inputListIndex > 0) {
        inputListIndex--;
        inputField.value = inputList[inputListIndex];
    }

    if (event.key == "Tab") {
        event.preventDefault();
        let text = inputField.value;

        if (autoCompleteList.length == 0) {
            let cursorPos = inputField.selectionStart;

            let start = cursorPos;
            let end = cursorPos;
            
            while (start > 0 && text.charAt(start - 1) != " ") start--;
            while (end < text.length && text.charAt(end) != " ") end++;

            let word = text.substring(start, end);

            autoCompleteList = user.channel.userList.map(aUser => aUser.nickName).filter(nickName => nickName.startsWith(word));
            autoCompleteList.unshift(word);
            autoCompleteListIndex = 1;
            autoCompleteStartPos = start;
        } else {
            autoCompleteListIndex = (autoCompleteListIndex + 1) % autoCompleteList.length;
        }

        if (autoCompleteList.length > 1) {
            let oldword = autoCompleteList[(autoCompleteListIndex + autoCompleteList.length - 1) % autoCompleteList.length];
            let word = autoCompleteList[autoCompleteListIndex];
            inputField.value = text.substring(0, autoCompleteStartPos) + word
                            + text.substring(autoCompleteStartPos + oldword.length);
            inputField.selectionStart = inputField.selectionEnd = autoCompleteStartPos + word.length;
        }

    }
});

inputField.addEventListener('input', function(event) {
    inputListIndex = 0;
    inputList[0] = inputField.value;
    autoCompleteList = [];
    autoCompleteListIndex = 0;
});

nickNameBtn.addEventListener('click', function(event) {
    event.preventDefault();
    if (!isValidNickName(nickNameField.value)) return;

    setNickName(nickNameField.value);
    msgSection.style.display = '';
    inputField.focus();
    
    document.getElementById('nickname-form').style.display = 'none';
    ws.send("NICK " + user.nickName);

    if (user.channel.name == null) {
        handleCommand('join', '#testchannel1');
    }

});

function setNickName(nickName) {
    user.nickName = nickName;
    nickNameLabel.innerText = nickName + ':';
}

function isValidNickName(nickName) {
    if (nickName.startsWith("#")) return false;
    if (nickName.match(/\s+/)) return false;
    return true;
}

connect(window.location.host);
nickNameField.focus();