local Players = game:GetService("Players")
local UserInputService = game:GetService("UserInputService")
local RunService = game:GetService("RunService")
local ProximityPromptService = game:GetService("ProximityPromptService")
local VirtualUser = game:GetService("VirtualUser")
local StarterGui = game:GetService("StarterGui")
local HttpService = game:GetService("HttpService")


local isMobile = UserInputService.TouchEnabled and not UserInputService.KeyboardEnabled


local player = Players.LocalPlayer


local buttonCooldowns = {}
local function withCooldown(key, handler, delay)
    local now = tick()
    delay = delay or 0.25
    if buttonCooldowns[key] and now - buttonCooldowns[key] < delay then
        return
    end
    buttonCooldowns[key] = now
    if type(handler) == "function" then
        handler()
    end
end


local function applySavedKeybindToButton(button)
    if not button or not button.Name then return end
    if not savedConfig or not savedConfig.keybinds then return end


    local stored = savedConfig.keybinds[button.Name]
    if type(stored) ~= "string" then return end


    local keyCode = Enum.KeyCode[stored]
    if keyCode then
        setButtonKeybind(button, keyCode)
    end
end


local ACTIVE_COLOR = Color3.fromRGB(0, 150, 0)
local CHECKMARK = "✅"
local character = player.Character or player.CharacterAdded:Wait()


local themes = {
    Black = Color3.fromRGB(0, 0, 0),
    Blue = Color3.fromRGB(0, 170, 255),
    Red = Color3.fromRGB(255, 70, 70),
    Green = Color3.fromRGB(70, 255, 140),
    Purple = Color3.fromRGB(180, 70, 255)
}


local currentThemeName = "Black"
local currentThemeColor = themes[currentThemeName]


local CONFIG_FILE = "Vexuhub_" .. player.Name .. "_config.json"
local canUseFS = typeof(isfile) == "function" and typeof(readfile) == "function" and typeof(writefile) == "function"


savedConfig = {
    theme = currentThemeName,
    keybinds = {}
}


local function loadConfig()
    if not canUseFS then return end
    if not isfile(CONFIG_FILE) then return end


    local ok, data = pcall(readfile, CONFIG_FILE)
    if not ok or not data or data == "" then return end


    local okDecode, decoded = pcall(function()
        return HttpService:JSONDecode(data)
    end)
    if not okDecode or type(decoded) ~= "table" then return end


    if type(decoded.theme) == "string" and themes[decoded.theme] then
        currentThemeName = decoded.theme
        currentThemeColor = themes[currentThemeName]
        savedConfig.theme = currentThemeName
    end


    if type(decoded.keybinds) == "table" then
        savedConfig.keybinds = decoded.keybinds
    end
end


local function saveConfig()
    if not canUseFS then return end


    savedConfig.theme = currentThemeName


    local okEncode, encoded = pcall(function()
        return HttpService:JSONEncode(savedConfig)
    end)
    if not okEncode then return end


    pcall(writefile, CONFIG_FILE, encoded)
end


loadConfig()


local function applyTheme(elements, color)
    for _, obj in ipairs(elements) do
        if obj:IsA("Frame") or obj:IsA("TextButton") or obj:IsA("TextLabel") then
            if obj.Name ~= "Background" then
                obj.BackgroundColor3 = color
            end
        end
        if obj:IsA("TextButton") or obj:IsA("TextLabel") then
            obj.TextColor3 = Color3.new(1, 1, 1)
        end
    end
end


local screenGui = Instance.new("ScreenGui")
screenGui.Name = "VexuhubUI"
screenGui.ResetOnSpawn = false
screenGui.Parent = player:WaitForChild("PlayerGui")


local mainFrame = Instance.new("Frame")
mainFrame.Name = "MainFrame"
mainFrame.Size = UDim2.new(0, 400, 0, 260)
mainFrame.Position = UDim2.new(0.5, -200, 0.5, -130)
mainFrame.BackgroundColor3 = Color3.fromRGB(10, 10, 10)
mainFrame.BorderSizePixel = 0
mainFrame.Active = true
mainFrame.Parent = screenGui


local topBar = Instance.new("Frame")
topBar.Name = "TopBar"
topBar.Size = UDim2.new(1, 0, 0, 30)
topBar.Position = UDim2.new(0, 0, 0, 0)
topBar.BackgroundColor3 = currentThemeColor
topBar.BorderSizePixel = 0
topBar.Parent = mainFrame


local titleLabel = Instance.new("TextLabel")
titleLabel.Name = "TitleLabel"
titleLabel.Size = UDim2.new(0, 120, 1, 0)
titleLabel.Position = UDim2.new(0, 8, 0, 0)
titleLabel.BackgroundTransparency = 1
titleLabel.Text = "XENT v0.1"
titleLabel.TextXAlignment = Enum.TextXAlignment.Left
titleLabel.Font = Enum.Font.GothamBold
titleLabel.TextSize = 18
titleLabel.TextColor3 = Color3.new(1, 1, 1)
titleLabel.Parent = topBar


local exitButton = Instance.new("TextButton")
exitButton.Name = "ExitButton"
exitButton.Size = UDim2.new(0, 40, 0, 24)
exitButton.Position = UDim2.new(1, -46, 0.5, -12)
exitButton.BackgroundColor3 = Color3.fromRGB(200, 50, 50)
exitButton.Text = "X"
exitButton.Font = Enum.Font.GothamBold
exitButton.TextSize = 18
exitButton.TextColor3 = Color3.new(1, 1, 1)
exitButton.Parent = topBar


local minimizeButton = Instance.new("TextButton")
minimizeButton.Name = "MinimizeButton"
minimizeButton.Size = UDim2.new(0, 34, 0, 24)
minimizeButton.Position = UDim2.new(1, -84, 0.5, -12)
minimizeButton.BackgroundColor3 = currentThemeColor
minimizeButton.Text = "-"
minimizeButton.Font = Enum.Font.GothamBold
minimizeButton.TextSize = 18
minimizeButton.TextColor3 = Color3.new(1, 1, 1)
minimizeButton.Parent = topBar


local tabBar = Instance.new("Frame")
tabBar.Name = "TabBar"
tabBar.Size = UDim2.new(1, 0, 0, 28)
tabBar.Position = UDim2.new(0, 0, 0, 30)
tabBar.BackgroundColor3 = Color3.fromRGB(15, 15, 15)
tabBar.BorderSizePixel = 0
tabBar.Parent = mainFrame


local function createTabButton(name, order)
    local btn = Instance.new("TextButton")
    btn.Name = name .. "Tab"
    btn.Size = UDim2.new(0, 90, 1, 0)
    btn.Position = UDim2.new(0, 8 + (order - 1) * 94, 0, 0)
    btn.BackgroundColor3 = currentThemeColor
    btn.Text = name
    btn.Font = Enum.Font.GothamSemibold
    btn.TextSize = 14
    btn.TextColor3 = Color3.new(1, 1, 1)
    btn.Parent = tabBar
    return btn
end


local mainTabButton = createTabButton("Main", 1)
local miscTabButton = createTabButton("Misc", 2)
local settingsTabButton = createTabButton("Settings", 3)


local pagesFrame = Instance.new("Frame")
pagesFrame.Name = "PagesFrame"
pagesFrame.Size = UDim2.new(1, -20, 1, -68)
pagesFrame.Position = UDim2.new(0, 10, 0, 60)
pagesFrame.BackgroundColor3 = Color3.fromRGB(5, 5, 5)
pagesFrame.BorderSizePixel = 0
pagesFrame.Parent = mainFrame


local function createPage(name)
    local page = Instance.new("Frame")
    page.Name = name .. "Page"
    page.Size = UDim2.new(1, 0, 1, 0)
    page.Position = UDim2.new(0, 0, 0, 0)
    page.BackgroundTransparency = 1
    page.Visible = false
    page.Parent = pagesFrame
    return page
end


local mainPage = createPage("Main")
local miscPage = createPage("Misc")
local settingsPage = createPage("Settings")


local restoreButton = Instance.new("TextButton")
restoreButton.Name = "RestoreButton"
-- make the restore button bigger so it's easier to grab
restoreButton.Size = UDim2.new(0, 44, 0, 44)
restoreButton.Position = UDim2.new(0, 10, 0, 10)
restoreButton.BackgroundColor3 = currentThemeColor
restoreButton.Text = "X"
restoreButton.Font = Enum.Font.GothamBold
restoreButton.TextSize = 20
restoreButton.TextColor3 = Color3.new(1, 1, 1)
restoreButton.Visible = false
restoreButton.Parent = screenGui


local restoreDragging = false
local restoreDragStart
local restoreStartPos
local restoreWasDragged = false


restoreButton.InputBegan:Connect(function(input)
    if input.UserInputType == Enum.UserInputType.MouseButton1 then
        restoreDragging = true
        restoreWasDragged = false
        restoreDragStart = input.Position
        restoreStartPos = restoreButton.Position
        input.Changed:Connect(function()
            if input.UserInputState == Enum.UserInputState.End then
                restoreDragging = false
                -- if the button was dragged, also reopen the hub when the drag ends
                if restoreWasDragged then
                    showHub()
                end
            end
        end)
    end
end)


UserInputService.InputChanged:Connect(function(input)
    if input.UserInputType == Enum.UserInputType.MouseMovement then
        if restoreDragging then
            local delta = input.Position - restoreDragStart
            -- consider it a drag once we've moved a little bit
            if math.abs(delta.X) > 2 or math.abs(delta.Y) > 2 then
                restoreWasDragged = true
            end
            restoreButton.Position = UDim2.new(
                restoreStartPos.X.Scale,
                restoreStartPos.X.Offset + delta.X,
                restoreStartPos.Y.Scale,
                restoreStartPos.Y.Offset + delta.Y
            )
        end
    end
end)


local function hideHub()
    mainFrame.Visible = false
    restoreButton.Visible = true
end


local function showHub()
    mainFrame.Visible = true
    restoreButton.Visible = false
end


minimizeButton.MouseButton1Click:Connect(hideHub)
restoreButton.MouseButton1Click:Connect(showHub)


local function showPage(pageName)
    mainPage.Visible = (pageName == "Main")
    miscPage.Visible = (pageName == "Misc")
    settingsPage.Visible = (pageName == "Settings")
end


mainTabButton.MouseButton1Click:Connect(function()
    showPage("Main")
end)


miscTabButton.MouseButton1Click:Connect(function()
    showPage("Misc")
end)


settingsTabButton.MouseButton1Click:Connect(function()
    showPage("Settings")
end)


showPage("Main")


local toggleIndicators = {} -- now stores per-button keybind TextBoxes
local buttonKeybinds = {}
local buttonClickHistory = {}
local buttonUpdateFunctions = {}
local buttonActions = {}


local function setButtonState(button, isActive)
    button.BackgroundColor3 = isActive and Color3.fromRGB(0, 255, 0) or currentThemeColor
end


local function refreshButtonLabel(button)
    local updater = buttonUpdateFunctions[button]
    if updater then
        updater()
    end
end


function setButtonKeybind(button, keyCode)
    buttonKeybinds[button] = keyCode
    if button and button.Name and savedConfig and savedConfig.keybinds then
        local key = button.Name
        if keyCode then
            local name = keyCode.Name or tostring(keyCode)
            savedConfig.keybinds[key] = name
        else
            savedConfig.keybinds[key] = nil
        end
        saveConfig()
    end
    -- keep the TextBox in sync with the stored keybind
    local keyBox = toggleIndicators[button]
    if keyBox and keyBox:IsA("TextBox") then
        if keyCode then
            keyBox.Text = keyCode.Name or tostring(keyCode)
        else
            keyBox.Text = ""
        end
    end
    refreshButtonLabel(button)
end


local function recordButtonClick(button)
    local now = tick()
    local history = buttonClickHistory[button]
    if not history then
        history = {}
        buttonClickHistory[button] = history
    end


    table.insert(history, now)
    if #history > 3 then
        table.remove(history, 1)
    end


    if #history == 3 and (history[3] - history[1]) <= 2.5 then
        buttonClickHistory[button] = {}
        setButtonKeybind(button, nil)
        if keybindTargetButton == button then
            keybindTargetButton = nil
        end
    end
end


local function attachIndicatorLabel(button)
    -- now creates a small TextBox on each button for keybind input
    if isMobile then return end
    if toggleIndicators[button] then return end


    local box = Instance.new("TextBox")
    box.Name = "KeyBox"
    box.BackgroundColor3 = Color3.fromRGB(20, 20, 20)
    box.BackgroundTransparency = 0.3
    -- small square on the far left edge of the button
    box.Size = UDim2.new(0, 18, 0, 18)
    box.Position = UDim2.new(0, 4, 0.5, -9)


    box.Font = button.Font
    box.TextSize = button.TextSize
    box.TextColor3 = button.TextColor3
    box.ClearTextOnFocus = false
    box.TextXAlignment = Enum.TextXAlignment.Center
    box.TextYAlignment = Enum.TextYAlignment.Center
    box.Text = ""

    box.Parent = button


    -- double-click the box quickly to reset the keybind
    local lastClick = 0
    box.InputBegan:Connect(function(input)
        if input.UserInputType == Enum.UserInputType.MouseButton1 then
            local now = tick()
            if now - lastClick <= 0.35 then
                lastClick = 0
                box.Text = ""
                setButtonKeybind(button, nil)
            else
                lastClick = now
            end
        end
    end)

    box.FocusLost:Connect(function()
        local text = box.Text or ""
        -- use only the first word and only letters (no numbers/symbols)
        local word = string.match(text, "^%s*([A-Za-z]+)%s*") or ""

        if word == "" then
            setButtonKeybind(button, nil)
            return
        end

        -- For single-letter keys (e.g. "f") use upper-case so it matches
        -- Enum.KeyCode.F. For multi-letter names (e.g. "LeftShift") keep
        -- the original casing so Enum.KeyCode lookup works.
        local lookup
        if #word == 1 then
            lookup = string.upper(word)
        else
            lookup = word
        end

        local keyCode = Enum.KeyCode[lookup]
        if keyCode then
            setButtonKeybind(button, keyCode)
        else
            -- invalid name, clear
            setButtonKeybind(button, nil)
        end
    end)

    toggleIndicators[button] = box
end


local function wrapText(button, active, text)
    -- no more [] prefix; just set the main label text
    button.Text = text
end


local keybindModeEnabled = false
local keybindTargetButton
local keybindPromptFrame
local keybindMainLabel
local keybindSubLabel
local keybindInputConnection


local function ensureKeybindPrompt()
    if keybindPromptFrame then return end


    keybindPromptFrame = Instance.new("Frame")
    keybindPromptFrame.Name = "KeybindPrompt"
    keybindPromptFrame.Size = UDim2.new(0, 380, 0, 40)
    keybindPromptFrame.Position = UDim2.new(0.5, -190, 0, -40)
    keybindPromptFrame.BackgroundTransparency = 1
    keybindPromptFrame.Parent = screenGui


    keybindMainLabel = Instance.new("TextLabel")
    keybindMainLabel.BackgroundTransparency = 1
    keybindMainLabel.Size = UDim2.new(1, 0, 0, 20)
    keybindMainLabel.Position = UDim2.new(0, 0, 0, 0)
    keybindMainLabel.Font = Enum.Font.GothamSemibold
    keybindMainLabel.TextSize = 16
    keybindMainLabel.TextColor3 = Color3.new(1, 1, 1)
    keybindMainLabel.TextStrokeColor3 = Color3.new(0, 0, 0)
    keybindMainLabel.TextStrokeTransparency = 0
    keybindMainLabel.Text = "click a button and press a word on your keyboard."
    keybindMainLabel.TextXAlignment = Enum.TextXAlignment.Center
    keybindMainLabel.TextYAlignment = Enum.TextYAlignment.Center
    keybindMainLabel.Parent = keybindPromptFrame


    keybindSubLabel = Instance.new("TextLabel")
    keybindSubLabel.BackgroundTransparency = 1
    keybindSubLabel.Size = UDim2.new(1, 0, 0, 18)
    keybindSubLabel.Position = UDim2.new(0, 0, 0, 20)
    keybindSubLabel.Font = Enum.Font.Gotham
    keybindSubLabel.TextSize = 14
    keybindSubLabel.TextColor3 = Color3.new(1, 1, 1)
    keybindSubLabel.TextStrokeColor3 = Color3.new(0, 0, 0)
    keybindSubLabel.TextStrokeTransparency = 0
    keybindSubLabel.Text = "click the same button 3 times to reset it"
    keybindSubLabel.TextXAlignment = Enum.TextXAlignment.Center
    keybindSubLabel.TextYAlignment = Enum.TextYAlignment.Center
    keybindSubLabel.Parent = keybindPromptFrame


    keybindPromptFrame.Visible = false
end


local function fadeOutKeybindPrompt()
    if not keybindPromptFrame or not keybindPromptFrame.Visible then return end


    task.spawn(function()
        for i = 0, 10 do
            local t = i / 10
            if keybindMainLabel then
                keybindMainLabel.TextTransparency = t
                keybindMainLabel.TextStrokeTransparency = t
            end
            if keybindSubLabel then
                keybindSubLabel.TextTransparency = t
                keybindSubLabel.TextStrokeTransparency = t
            end
            task.wait(0.03)
        end
        if keybindPromptFrame then
            keybindPromptFrame.Visible = false
        end
    end)
end


local function showKeybindPrompt()
    ensureKeybindPrompt()
    keybindPromptFrame.Visible = true
    keybindMainLabel.TextTransparency = 0
    keybindMainLabel.TextStrokeTransparency = 0
    keybindSubLabel.TextTransparency = 0
    keybindSubLabel.TextStrokeTransparency = 0
    keybindPromptFrame.Position = UDim2.new(0.5, -190, 0, -40)


    task.spawn(function()
        for i = 0, 10 do
            local t = i / 10
            local y = -40 + (50 * t)
            if keybindPromptFrame then
                keybindPromptFrame.Position = UDim2.new(0.5, -190, 0, y)
            end
            task.wait(0.03)
        end
    end)
end


local function setKeybindMode(enabled)
    if keybindModeEnabled == enabled then return end
    keybindModeEnabled = enabled
    keybindTargetButton = nil
    if enabled then
        showKeybindPrompt()
    else
        fadeOutKeybindPrompt()
    end
end


keybindInputConnection = UserInputService.InputBegan:Connect(function(input, gameProcessed)
    if input.UserInputType ~= Enum.UserInputType.Keyboard then
        return
    end


    local keyCode = input.KeyCode
    if keyCode == Enum.KeyCode.Unknown then
        return
    end


    -- While in keybind setup mode, always capture the key and do NOT
    -- trigger any existing hotkeys.
    if keybindModeEnabled then
        if keybindTargetButton then
            setButtonKeybind(keybindTargetButton, keyCode)
            setKeybindMode(false)
        end
        return
    end


    -- Outside of keybind mode, ignore inputs Roblox already processed
    -- (e.g. chat) to avoid conflicts.
    if gameProcessed then return end


    for button, boundKey in pairs(buttonKeybinds) do
        if boundKey == keyCode then
            -- Extra safety: only trigger if this button still has
            -- a key stored in the config. If the keybind was reset
            -- (removed from savedConfig.keybinds), ignore this.
            local isStillBound = true
            if savedConfig and savedConfig.keybinds then
                local stored = savedConfig.keybinds[button.Name]
                if stored == nil then
                    isStillBound = false
                end
            end


            if isStillBound then
                local action = buttonActions[button]
                if action then
                    withCooldown("keybind_" .. tostring(button), function()
                        action()
                    end, 0.15)
                end
            end
        end
    end
end)


local noAnimationsEnabled = false
local noAnimJoints = {}
local noAnimJointConnection


-- No animations button (main page)
local rawrButton = Instance.new("TextButton")
rawrButton.Name = "NoAnimationsButton"
rawrButton.Size = UDim2.new(0, 180, 0, 32)
rawrButton.Position = UDim2.new(0, 10, 0, 10)
rawrButton.BackgroundColor3 = currentThemeColor
rawrButton.Text = "No animations"
rawrButton.Font = Enum.Font.GothamBold
rawrButton.TextSize = 14
rawrButton.TextColor3 = Color3.new(1, 1, 1)
rawrButton.TextXAlignment = Enum.TextXAlignment.Center
rawrButton.Parent = mainPage


attachIndicatorLabel(rawrButton)


local function updateNoAnimationsText()
	wrapText(rawrButton, noAnimationsEnabled, "No animations")
	setButtonState(rawrButton, noAnimationsEnabled)
end


local function setCharacterAnimationsEnabled(char, enabled)
    if not char then return end
    local animate = char:FindFirstChild("Animate")
    if animate then
        animate.Disabled = not enabled
    end
    local humanoid = char:FindFirstChildOfClass("Humanoid")
    if humanoid then
        local animator = humanoid:FindFirstChildOfClass("Animator")
        if animator then
            for _, track in ipairs(animator:GetPlayingAnimationTracks()) do
                if enabled then
                    track:Play()
                else
                    track:Stop()
                end
            end
        end
    end
end


local function cacheNoAnimJoints(char)
    table.clear(noAnimJoints)
    if not char then return end


    for _, inst in ipairs(char:GetDescendants()) do
        if inst:IsA("Motor6D") then
            table.insert(noAnimJoints, {
                joint = inst,
                C0 = inst.C0,
                C1 = inst.C1
            })
        end
    end
end


local function startNoAnimJointLock()
    if noAnimJointConnection then
        noAnimJointConnection:Disconnect()
        noAnimJointConnection = nil
    end


    noAnimJointConnection = RunService.RenderStepped:Connect(function()
        if not noAnimationsEnabled then return end


        for _, data in ipairs(noAnimJoints) do
            local joint = data.joint
            if joint and joint.Parent then
                if joint.C0 ~= data.C0 then
                    joint.C0 = data.C0
                end
                if joint.C1 ~= data.C1 then
                    joint.C1 = data.C1
                end
            end
        end
    end)
end


local function stopNoAnimJointLock()
    if noAnimJointConnection then
        noAnimJointConnection:Disconnect()
        noAnimJointConnection = nil
    end
end


local function applyNoAnimationsState()
    if noAnimationsEnabled then
        cacheNoAnimJoints(character)
        setCharacterAnimationsEnabled(character, false)
        startNoAnimJointLock()
    else
        stopNoAnimJointLock()
        setCharacterAnimationsEnabled(character, true)
    end
end


local function toggleNoAnimations()
	noAnimationsEnabled = not noAnimationsEnabled
	applyNoAnimationsState()
	updateNoAnimationsText()
end


rawrButton.MouseButton1Click:Connect(function()
    recordButtonClick(rawrButton)
    if keybindModeEnabled then
        keybindTargetButton = rawrButton
        return
    end


    withCooldown("noAnimations", function()
        toggleNoAnimations()
    end)
end)


buttonUpdateFunctions[rawrButton] = updateNoAnimationsText
buttonActions[rawrButton] = toggleNoAnimations
applySavedKeybindToButton(rawrButton)


local autoBlockButton = Instance.new("TextButton")
autoBlockButton.Name = "AutoBlockButton"
autoBlockButton.Size = UDim2.new(0, 180, 0, 32)
autoBlockButton.Position = UDim2.new(0, 10, 0, 52)
autoBlockButton.BackgroundColor3 = currentThemeColor
autoBlockButton.Text = "Auto block"
autoBlockButton.Font = Enum.Font.GothamBold
autoBlockButton.TextSize = 14
autoBlockButton.TextColor3 = Color3.new(1, 1, 1)
autoBlockButton.TextXAlignment = Enum.TextXAlignment.Center
autoBlockButton.Parent = mainPage


attachIndicatorLabel(autoBlockButton)


local function updateAutoBlockText()
    -- Auto block is a one-shot action, not a toggle, so we always
    -- show it as OFF (no checkmark).
    wrapText(autoBlockButton, false, "Auto block")
    setButtonState(autoBlockButton, false)
end


local function getNearestPlayerForBlock()
    local myChar = character
    if not myChar then return nil end

    local myRoot = myChar:FindFirstChild("HumanoidRootPart")
        or myChar:FindFirstChild("Torso")
        or myChar:FindFirstChild("UpperTorso")
    if not myRoot then return nil end

    local nearestPlayer
    local nearestDist
    for _, plr in ipairs(Players:GetPlayers()) do
        if plr ~= player and plr.Character then
            local root = plr.Character:FindFirstChild("HumanoidRootPart")
                or plr.Character:FindFirstChild("Torso")
                or plr.Character:FindFirstChild("UpperTorso")
            if root then
                local dist = (root.Position - myRoot.Position).Magnitude
                if not nearestDist or dist < nearestDist then
                    nearestDist = dist
                    nearestPlayer = plr
                end
            end
        end
    end

    -- Fallback: if we couldn't get a distance (e.g. no roots), just
    -- pick any other player in the server.
    if not nearestPlayer then
        for _, plr in ipairs(Players:GetPlayers()) do
            if plr ~= player then
                nearestPlayer = plr
                break
            end
        end
    end

    return nearestPlayer
end


local function toggleAutoBlock()
    -- Just open Roblox's built-in "Block <player>?" prompt for the
    -- nearest other player.
    local target = getNearestPlayerForBlock()
    if not target then return end

    pcall(function()
        if StarterGui and StarterGui.SetCore then
            StarterGui:SetCore("PromptBlockPlayer", target)
        end
    end)
end


autoBlockButton.MouseButton1Click:Connect(function()
    recordButtonClick(autoBlockButton)
    if keybindModeEnabled then
        keybindTargetButton = autoBlockButton
        return
    end

    withCooldown("autoBlock", function()
        toggleAutoBlock()
    end)
end)


buttonUpdateFunctions[autoBlockButton] = updateAutoBlockText
buttonActions[autoBlockButton] = toggleAutoBlock
applySavedKeybindToButton(autoBlockButton)


local jumpHopButton = Instance.new("TextButton")
jumpHopButton.Name = "JumpHopButton"

jumpHopButton.Size = UDim2.new(0, 180, 0, 32)
jumpHopButton.Position = UDim2.new(0, 10, 0, 94)
jumpHopButton.BackgroundColor3 = currentThemeColor
jumpHopButton.Text = "Jump Hop"
jumpHopButton.Font = Enum.Font.GothamSemibold
jumpHopButton.TextSize = 14
jumpHopButton.TextColor3 = Color3.new(1, 1, 1)
jumpHopButton.TextXAlignment = Enum.TextXAlignment.Center
jumpHopButton.Parent = mainPage


attachIndicatorLabel(jumpHopButton)


local jumpHopEnabled = false
local jumpHopReady = false
local jumpHopConnection
local jumpHopInputConnection
local JUMP_HOP_VELOCITY = 48
local hopCount = 0
local MAX_HOPS = 6


local function updateJumpHopText()
    wrapText(jumpHopButton, jumpHopEnabled, "Jump Hop")
    setButtonState(jumpHopButton, jumpHopEnabled)
end


local function startJumpHop()
    if jumpHopConnection then
        jumpHopConnection:Disconnect()
        jumpHopConnection = nil
    end

    if jumpHopInputConnection then
        jumpHopInputConnection:Disconnect()
        jumpHopInputConnection = nil
    end

    jumpHopConnection = RunService.Heartbeat:Connect(function()
        if not character then
            return
        end

        local humanoid = character:FindFirstChildOfClass("Humanoid")
        local rootPart = character:FindFirstChild("HumanoidRootPart")
        if not humanoid or not rootPart then
            return
        end

        local isOnGround = humanoid.FloorMaterial ~= Enum.Material.Air
        local falling = rootPart.Velocity.Y < -1

        if isOnGround then
            hopCount = 0
            jumpHopReady = false
            return
        end

        if falling and hopCount < MAX_HOPS then
            jumpHopReady = true
        end
    end)

    jumpHopInputConnection = UserInputService.JumpRequest:Connect(function()
        if not jumpHopEnabled then
            return
        end

        if jumpHopReady and hopCount < MAX_HOPS then
            local rootPart = character and character:FindFirstChild("HumanoidRootPart")
            if rootPart then
                rootPart.Velocity = Vector3.new(rootPart.Velocity.X, JUMP_HOP_VELOCITY, rootPart.Velocity.Z)
                jumpHopReady = false
                hopCount = hopCount + 1
            end
        end
    end)
end


local function stopJumpHop()
    if jumpHopConnection then
        jumpHopConnection:Disconnect()
        jumpHopConnection = nil
    end

    if jumpHopInputConnection then
        jumpHopInputConnection:Disconnect()
        jumpHopInputConnection = nil
    end

    jumpHopReady = false
end


local function toggleJumpHop()
    jumpHopEnabled = not jumpHopEnabled
    if jumpHopEnabled then
        startJumpHop()
    else
        stopJumpHop()
    end
    updateJumpHopText()
end


jumpHopButton.MouseButton1Click:Connect(function()
    recordButtonClick(jumpHopButton)
    if keybindModeEnabled then
        keybindTargetButton = jumpHopButton
        return
    end

    withCooldown("jumpHop", function()
        toggleJumpHop()
    end)
end)


buttonUpdateFunctions[jumpHopButton] = updateJumpHopText
buttonActions[jumpHopButton] = toggleJumpHop
applySavedKeybindToButton(jumpHopButton)


local speedIncreaseButton = Instance.new("TextButton")
speedIncreaseButton.Name = "SpeedIncreaseButton"
speedIncreaseButton.Size = UDim2.new(0, 180, 0, 32)
speedIncreaseButton.Position = UDim2.new(0, 10, 0, 136)
speedIncreaseButton.BackgroundColor3 = currentThemeColor
speedIncreaseButton.Text = "Speed increase"
speedIncreaseButton.Font = Enum.Font.GothamSemibold
speedIncreaseButton.TextSize = 14
speedIncreaseButton.TextColor3 = Color3.new(1, 1, 1)
speedIncreaseButton.TextXAlignment = Enum.TextXAlignment.Center
speedIncreaseButton.Parent = mainPage


attachIndicatorLabel(speedIncreaseButton)


local speedToggled = false
local speedConnection
local speedWalkConnection
local originalWalkSpeed = 16
local speedMultiplier = 1.25


local function updateSpeedText()
    wrapText(speedIncreaseButton, speedToggled, "Speed increase")
    setButtonState(speedIncreaseButton, speedToggled)
end


local function updateSpeed()
    if not character then return end
    local humanoid = character:FindFirstChildOfClass("Humanoid")
    if not humanoid then return end
    if speedToggled then
        -- Keep boosted speed locked to originalWalkSpeed * speedMultiplier
        local targetSpeed = originalWalkSpeed * speedMultiplier
        if math.abs(humanoid.WalkSpeed - targetSpeed) > 0.5 then
            humanoid.WalkSpeed = targetSpeed
        end
    else
        -- When disabled, ensure we are at the original (saved) walk speed
        if math.abs(humanoid.WalkSpeed - originalWalkSpeed) > 0.5 then
            humanoid.WalkSpeed = originalWalkSpeed
        end
    end
end


local function startSpeedLoop()
    if speedConnection then
        speedConnection:Disconnect()
    end
    speedConnection = RunService.Heartbeat:Connect(updateSpeed)

    -- Also react whenever something else changes WalkSpeed
    if character then
        local humanoid = character:FindFirstChildOfClass("Humanoid")
        if humanoid then
            if speedWalkConnection then
                speedWalkConnection:Disconnect()
                speedWalkConnection = nil
            end
            speedWalkConnection = humanoid:GetPropertyChangedSignal("WalkSpeed"):Connect(function()
                if speedToggled then
                    updateSpeed()
                end
            end)
        end
    end
end


local function stopSpeedLoop()
    if speedConnection then
        speedConnection:Disconnect()
        speedConnection = nil
    end
    if speedWalkConnection then
        speedWalkConnection:Disconnect()
        speedWalkConnection = nil
    end
end


local function toggleSpeedIncrease()
    speedToggled = not speedToggled
    setButtonState(speedIncreaseButton, speedToggled)

    if speedToggled then
        local humanoid = character and character:FindFirstChildOfClass("Humanoid")
        if humanoid then
            originalWalkSpeed = humanoid.WalkSpeed
        end
        updateSpeed()
        startSpeedLoop()
    else
        local humanoid = character and character:FindFirstChildOfClass("Humanoid")
        if humanoid then
            humanoid.WalkSpeed = originalWalkSpeed
        end
        stopSpeedLoop()
    end

    updateSpeedText()
end


speedIncreaseButton.MouseButton1Click:Connect(function()
    recordButtonClick(speedIncreaseButton)
    if keybindModeEnabled then
        keybindTargetButton = speedIncreaseButton
        return
    end

    withCooldown("speed", function()
        toggleSpeedIncrease()
    end)
end)


buttonUpdateFunctions[speedIncreaseButton] = updateSpeedText
buttonActions[speedIncreaseButton] = toggleSpeedIncrease
applySavedKeybindToButton(speedIncreaseButton)


updateSpeedText()


local instantPickupButton = Instance.new("TextButton")
instantPickupButton.Name = "InstantPickupButton"
instantPickupButton.Size = UDim2.new(0, 160, 0, 32)
instantPickupButton.Position = UDim2.new(0, 210, 0, 10)
instantPickupButton.BackgroundColor3 = currentThemeColor
instantPickupButton.Text = "Auto Grab"
instantPickupButton.Font = Enum.Font.GothamSemibold
instantPickupButton.TextSize = 14
instantPickupButton.TextColor3 = Color3.new(1, 1, 1)
instantPickupButton.TextXAlignment = Enum.TextXAlignment.Center
instantPickupButton.Parent = mainPage


attachIndicatorLabel(instantPickupButton)


local autoPickupEnabled = false
local autoPromptConnection


local function updateAutoPickupText()
    wrapText(instantPickupButton, autoPickupEnabled, "Auto Grab")
    setButtonState(instantPickupButton, autoPickupEnabled)
end


local function onPromptShown(prompt, inputType)
    if not autoPickupEnabled then return end
    if not prompt or not prompt.Parent or not prompt.Enabled then return end


    -- Only auto-pickup prompts that use the E key, to match "hold E" behavior
    if prompt.KeyboardKeyCode ~= Enum.KeyCode.E then return end


    local duration = prompt.HoldDuration or 0
    -- Only handle prompts that actually require holding; skip instant (0 sec) prompts
    if duration <= 0 then
        return
    end


    task.spawn(function()
        pcall(function()
            prompt:InputHoldBegin()
            task.delay(duration + 0.05, function()
                if autoPickupEnabled and prompt and prompt.Parent and prompt.Enabled then
                    prompt:InputHoldEnd()
                end
            end)
        end)
    end)
end


local function startAutoPickup()
    if autoPromptConnection then
        autoPromptConnection:Disconnect()
        autoPromptConnection = nil
    end
    autoPromptConnection = ProximityPromptService.PromptShown:Connect(onPromptShown)
end


local function stopAutoPickup()
    if autoPromptConnection then
        autoPromptConnection:Disconnect()
        autoPromptConnection = nil
    end
end


local function toggleAutoPickup()
    autoPickupEnabled = not autoPickupEnabled
    if autoPickupEnabled then
        startAutoPickup()
    else
        stopAutoPickup()
    end
    updateAutoPickupText()
end


instantPickupButton.MouseButton1Click:Connect(function()
    recordButtonClick(instantPickupButton)
    if keybindModeEnabled then
        keybindTargetButton = instantPickupButton
        return
    end


    withCooldown("autoPickup", function()
        toggleAutoPickup()
    end)
end)


buttonUpdateFunctions[instantPickupButton] = updateAutoPickupText
buttonActions[instantPickupButton] = toggleAutoPickup
applySavedKeybindToButton(instantPickupButton)


updateAutoPickupText()


local moreFpsButton = Instance.new("TextButton")
moreFpsButton.Name = "MoreFpsButton"
moreFpsButton.Size = UDim2.new(0, 160, 0, 32)
moreFpsButton.Position = UDim2.new(0, 210, 0, 52)
moreFpsButton.BackgroundColor3 = currentThemeColor
moreFpsButton.Text = "More FPS"
moreFpsButton.Font = Enum.Font.GothamSemibold
moreFpsButton.TextSize = 14
moreFpsButton.TextColor3 = Color3.new(1, 1, 1)
moreFpsButton.TextXAlignment = Enum.TextXAlignment.Center
moreFpsButton.Parent = mainPage


attachIndicatorLabel(moreFpsButton)


local moreFpsEnabled = false
local savedLightingProps = {}
local moreFpsDisabledEffects = {}


local function applyMoreFps()
    local lighting = game:GetService("Lighting")
    if not lighting then return end


    if not next(savedLightingProps) then
        savedLightingProps.GlobalShadows = lighting.GlobalShadows
        savedLightingProps.Brightness = lighting.Brightness
        savedLightingProps.EnvironmentDiffuseScale = lighting.EnvironmentDiffuseScale
        savedLightingProps.EnvironmentSpecularScale = lighting.EnvironmentSpecularScale
    end


    lighting.GlobalShadows = false
    lighting.Brightness = math.min(lighting.Brightness, 2)
    lighting.EnvironmentDiffuseScale = 0
    lighting.EnvironmentSpecularScale = 0


    for _, inst in ipairs(lighting:GetDescendants()) do
        if inst:IsA("BloomEffect") or inst:IsA("ColorCorrectionEffect")
            or inst:IsA("DepthOfFieldEffect") or inst:IsA("SunRaysEffect")
            or inst:IsA("BlurEffect") then
            if moreFpsDisabledEffects[inst] == nil then
                moreFpsDisabledEffects[inst] = inst.Enabled
            end
            inst.Enabled = false
        end
    end
end


local function restoreMoreFps()
    local lighting = game:GetService("Lighting")
    if lighting and next(savedLightingProps) then
        lighting.GlobalShadows = savedLightingProps.GlobalShadows
        lighting.Brightness = savedLightingProps.Brightness
        lighting.EnvironmentDiffuseScale = savedLightingProps.EnvironmentDiffuseScale
        lighting.EnvironmentSpecularScale = savedLightingProps.EnvironmentSpecularScale
    end


    for inst, wasEnabled in pairs(moreFpsDisabledEffects) do
        if inst and inst.Parent then
            inst.Enabled = wasEnabled
        end
    end
end


local function updateMoreFpsText()
    wrapText(moreFpsButton, moreFpsEnabled, "More FPS")
    setButtonState(moreFpsButton, moreFpsEnabled)
end


local function toggleMoreFps()
    moreFpsEnabled = not moreFpsEnabled
    if moreFpsEnabled then
        applyMoreFps()
    else
        restoreMoreFps()
    end
    updateMoreFpsText()
end


moreFpsButton.MouseButton1Click:Connect(function()
    recordButtonClick(moreFpsButton)
    if keybindModeEnabled then
        keybindTargetButton = moreFpsButton
        return
    end


    withCooldown("moreFps", function()
        toggleMoreFps()
    end)
end)


buttonUpdateFunctions[moreFpsButton] = updateMoreFpsText
buttonActions[moreFpsButton] = toggleMoreFps
applySavedKeybindToButton(moreFpsButton)


updateMoreFpsText()


local showPlayersButton = Instance.new("TextButton")
showPlayersButton.Name = "ShowPlayersButton"
showPlayersButton.Size = UDim2.new(0, 180, 0, 32)
showPlayersButton.Position = UDim2.new(0, 10, 0, 10)
showPlayersButton.BackgroundColor3 = currentThemeColor
showPlayersButton.Text = "Show Players"
showPlayersButton.Font = Enum.Font.GothamBold
showPlayersButton.TextSize = 14
showPlayersButton.TextColor3 = Color3.new(1, 1, 1)
showPlayersButton.TextXAlignment = Enum.TextXAlignment.Center
showPlayersButton.Parent = miscPage


attachIndicatorLabel(showPlayersButton)


local xrayButton = Instance.new("TextButton")
xrayButton.Name = "XrayButton"
xrayButton.Size = UDim2.new(0, 160, 0, 32)
xrayButton.Position = UDim2.new(0, 210, 0, 10)
xrayButton.BackgroundColor3 = currentThemeColor
xrayButton.Text = "X-ray"
xrayButton.Font = Enum.Font.GothamBold
xrayButton.TextSize = 14
xrayButton.TextColor3 = Color3.new(1, 1, 1)
xrayButton.TextXAlignment = Enum.TextXAlignment.Center
xrayButton.Parent = miscPage


attachIndicatorLabel(xrayButton)


local brainrotEspButton = Instance.new("TextButton")
brainrotEspButton.Name = "BrainrotESPButton"
brainrotEspButton.Size = UDim2.new(0, 180, 0, 32)
brainrotEspButton.Position = UDim2.new(0, 10, 0, 52)
brainrotEspButton.BackgroundColor3 = currentThemeColor
brainrotEspButton.Text = "Brainrot ESP"
brainrotEspButton.Font = Enum.Font.GothamBold
brainrotEspButton.TextSize = 14
brainrotEspButton.TextColor3 = Color3.new(1, 1, 1)
brainrotEspButton.TextXAlignment = Enum.TextXAlignment.Center
brainrotEspButton.Parent = miscPage


attachIndicatorLabel(brainrotEspButton)


local invisibleWallsButton = Instance.new("TextButton")
invisibleWallsButton.Name = "InvisibleWallsButton"
invisibleWallsButton.Size = UDim2.new(0, 180, 0, 32)
invisibleWallsButton.Position = UDim2.new(0, 10, 0, 94)
invisibleWallsButton.BackgroundColor3 = currentThemeColor
invisibleWallsButton.Text = "Invisible walls"
invisibleWallsButton.Font = Enum.Font.GothamBold
invisibleWallsButton.TextSize = 14
invisibleWallsButton.TextColor3 = Color3.new(1, 1, 1)
invisibleWallsButton.TextXAlignment = Enum.TextXAlignment.Center
invisibleWallsButton.Parent = miscPage

attachIndicatorLabel(invisibleWallsButton)


local lagServerButton = Instance.new("TextButton")
lagServerButton.Name = "LagServerButton"
lagServerButton.Size = UDim2.new(0, 180, 0, 32)
lagServerButton.Position = UDim2.new(0, 10, 0, 136)
lagServerButton.BackgroundColor3 = currentThemeColor
lagServerButton.Text = "Lag server"
lagServerButton.Font = Enum.Font.GothamBold
lagServerButton.TextSize = 14
lagServerButton.TextColor3 = Color3.new(1, 1, 1)
lagServerButton.TextXAlignment = Enum.TextXAlignment.Center
lagServerButton.Parent = miscPage


attachIndicatorLabel(lagServerButton)


-- SETTINGS PAGE UI
local changeThemeButton = Instance.new("TextButton")
changeThemeButton.Name = "ChangeThemeButton"
changeThemeButton.Size = UDim2.new(0, 180, 0, 32)
changeThemeButton.Position = UDim2.new(0, 10, 0, 10)
changeThemeButton.BackgroundColor3 = currentThemeColor
changeThemeButton.Text = "Change Theme Color"
changeThemeButton.Font = Enum.Font.GothamBold
changeThemeButton.TextSize = 14
changeThemeButton.TextColor3 = Color3.new(1, 1, 1)
changeThemeButton.Parent = settingsPage


local dropdownFrame = Instance.new("ScrollingFrame")
dropdownFrame.Name = "ThemeDropdown"
dropdownFrame.Size = UDim2.new(0, 180, 0, 4 + 4 * 26)
dropdownFrame.Position = UDim2.new(0, 10, 0, 48)
dropdownFrame.BackgroundColor3 = Color3.fromRGB(15, 15, 25)
dropdownFrame.BorderSizePixel = 0
dropdownFrame.Visible = false
dropdownFrame.CanvasSize = UDim2.new(0, 0, 0, 4 + 4 * 26)
dropdownFrame.ScrollBarThickness = 5
dropdownFrame.ScrollBarImageColor3 = Color3.fromRGB(200, 200, 200)
dropdownFrame.Parent = settingsPage


local themeButtons = {}
local themeIndex = 0
for name, color in pairs(themes) do
	themeIndex = themeIndex + 1
	local btn = Instance.new("TextButton")
	btn.Name = name .. "ThemeButton"
	btn.Size = UDim2.new(1, -8, 0, 24)
	btn.Position = UDim2.new(0, 4, 0, 2 + (themeIndex - 1) * 26)
	btn.BackgroundColor3 = color
	btn.Text = name
	btn.Font = Enum.Font.GothamBold
	btn.TextSize = 14
	btn.TextColor3 = Color3.new(1, 1, 1)
	btn.Parent = dropdownFrame
	themeButtons[name] = btn
end


dropdownFrame.CanvasSize = UDim2.new(0, 0, 0, 4 + themeIndex * 26)


local copyDiscordButton = Instance.new("TextButton")
copyDiscordButton.Name = "CopyDiscordButton"
copyDiscordButton.Size = UDim2.new(0, 180, 0, 32)
copyDiscordButton.Position = UDim2.new(0, 10, 0, 48)
copyDiscordButton.BackgroundColor3 = currentThemeColor
copyDiscordButton.Text = "Copy Discord link"
copyDiscordButton.Font = Enum.Font.GothamBold
copyDiscordButton.TextSize = 14
copyDiscordButton.TextColor3 = Color3.new(1, 1, 1)
copyDiscordButton.Parent = settingsPage


local autoKickButton = Instance.new("TextButton")
autoKickButton.Name = "AutoKickButton"
autoKickButton.Size = UDim2.new(0, 180, 0, 32)
autoKickButton.Position = UDim2.new(0, 200, 0, 10)
autoKickButton.BackgroundColor3 = currentThemeColor
autoKickButton.Text = "Auto kick after steal"
autoKickButton.Font = Enum.Font.GothamBold
autoKickButton.TextSize = 14
autoKickButton.TextColor3 = Color3.new(1, 1, 1)
autoKickButton.TextXAlignment = Enum.TextXAlignment.Center
autoKickButton.Parent = settingsPage


local keybindModeButton = Instance.new("TextButton")
keybindModeButton.Name = "KeybindModeButton"
keybindModeButton.Size = UDim2.new(0, 200, 0, 32)
keybindModeButton.Position = UDim2.new(0, 10, 0, 132)
keybindModeButton.BackgroundColor3 = currentThemeColor
keybindModeButton.Text = "Add keybind to button"
keybindModeButton.Font = Enum.Font.GothamBold
keybindModeButton.TextSize = 14
keybindModeButton.TextColor3 = Color3.new(1, 1, 1)
keybindModeButton.TextXAlignment = Enum.TextXAlignment.Center
keybindModeButton.Parent = settingsPage


-- keybind mode button is deprecated; keep it hidden on all platforms
keybindModeButton.Visible = false
keybindModeButton.Active = false


local showPlayersEnabled = false
local invisibleWallsEnabled = false
local lagServerEnabled = false
local playerHighlightData = {}
local playerCharacterConnections = {}
local playersAddedConnection
local playersRemovingConnection


local invisibleWallParts = {}


local brainrotBillboard
local brainrotEspEnabled = false


local xrayEnabled = false
local xrayConnection
local xrayFromAttachment
local xrayBrainrotAttachment
local xrayNicknameAttachment
local xrayBrainrotBeam
local xrayNicknameBeam


local function clearXrayVisuals()
    if xrayConnection then
        xrayConnection:Disconnect()
        xrayConnection = nil
    end
    if xrayBrainrotBeam then
        xrayBrainrotBeam:Destroy()
        xrayBrainrotBeam = nil
    end
    if xrayNicknameBeam then
        xrayNicknameBeam:Destroy()
        xrayNicknameBeam = nil
    end
    if xrayFromAttachment then
        xrayFromAttachment:Destroy()
        xrayFromAttachment = nil
    end
    if xrayBrainrotAttachment then
        xrayBrainrotAttachment:Destroy()
        xrayBrainrotAttachment = nil
    end
    if xrayNicknameAttachment then
        xrayNicknameAttachment:Destroy()
        xrayNicknameAttachment = nil
    end
end


local function updateXrayText()
    wrapText(xrayButton, xrayEnabled, "X-ray")
    setButtonState(xrayButton, xrayEnabled)
end


local function startXray()
    clearXrayVisuals()


    -- Inline search for the highest brainrot source so we don't depend on
    -- any external function reference when X-ray runs.
    local bestGui = nil
    local bestValue = nil
    local inspected = 0
    for _, inst in ipairs(workspace:GetDescendants()) do
        if inst:IsA("TextLabel") or inst:IsA("TextBox") then
            local label = inst
            local text = label.Text
            if type(text) == "string" and text ~= "" and string.find(text, "M/s", 1, true) then
                local numStr = string.match(text, "%$?%s*([%d%.]+)%s*[Mm]/s")
                if numStr then
                    local value = tonumber(numStr)
                    if value then
                        local guiAncestor = label:FindFirstAncestorWhichIsA("BillboardGui")
                            or label:FindFirstAncestorWhichIsA("SurfaceGui")

                        if guiAncestor and (not bestValue or value > bestValue) then
                            bestValue = value
                            bestGui = guiAncestor
                        end
                    end
                end
            end
        end

        inspected = inspected + 1
        if inspected % 400 == 0 then
            task.wait()
        end
    end

    if not bestGui then
        return
    end

    local char = character
    if not char then
        return
    end
    local root = char:FindFirstChild("HumanoidRootPart")
        or char:FindFirstChild("Torso")
        or char:FindFirstChild("UpperTorso")
    if not root then
        return
    end


    local targetPart
    local adornee = bestGui.Adornee
    if adornee and adornee:IsA("BasePart") then
        targetPart = adornee
    else
        targetPart = bestGui:FindFirstAncestorWhichIsA("BasePart")
        if not targetPart then
            local model = bestGui:FindFirstAncestorWhichIsA("Model")
            if model then
                targetPart = model.PrimaryPart or model:FindFirstChildWhichIsA("BasePart")
            end
        end
    end
    if not targetPart then
        return
    end


    xrayFromAttachment = Instance.new("Attachment")
    xrayFromAttachment.Name = "XentXrayFromAttachment"
    xrayFromAttachment.Parent = root


    xrayBrainrotAttachment = Instance.new("Attachment")
    xrayBrainrotAttachment.Name = "XentXrayBrainrotAttachment"
    xrayBrainrotAttachment.Position = Vector3.new(-2, -5, 0)
    xrayBrainrotAttachment.Parent = targetPart


    local nicknameParent
    local myNameLower = string.lower(player.Name)
    local displayNameLower = string.lower(player.DisplayName or player.Name)
    for _, inst in ipairs(workspace:GetDescendants()) do
        if inst:IsA("SurfaceGui") or inst:IsA("BillboardGui") then
            for _, guiChild in ipairs(inst:GetDescendants()) do
                if guiChild:IsA("TextLabel") or guiChild:IsA("TextBox") then
                    local text = guiChild.Text
                    if type(text) == "string" and text ~= "" then
                        local lower = string.lower(text)
                        local hasBase = string.find(lower, "base", 1, true) ~= nil
                        local hasUserName = string.find(lower, myNameLower, 1, true) ~= nil
                        local hasDisplayName = string.find(lower, displayNameLower, 1, true) ~= nil
                        -- Look for any text that contains either the username or display name
                        -- plus the word "base" (e.g. "7euroo's Base").
                        if hasBase and (hasUserName or hasDisplayName) then
                            local parentPart = inst.Adornee or inst.Parent
                            if parentPart and parentPart:IsA("BasePart") then
                                nicknameParent = parentPart
                                break
                            end
                        end
                    end
                end
            end
        end
        if nicknameParent then
            break
        end
    end


    xrayNicknameAttachment = Instance.new("Attachment")
    xrayNicknameAttachment.Name = "XentXrayNicknameAttachment"
    xrayNicknameAttachment.Position = Vector3.new(0, -15, 0)
    xrayNicknameAttachment.Parent = nicknameParent or root


    xrayBrainrotBeam = Instance.new("Beam")
    xrayBrainrotBeam.Name = "XentXrayBrainrotBeam"
    xrayBrainrotBeam.Attachment0 = xrayFromAttachment
    xrayBrainrotBeam.Attachment1 = xrayBrainrotAttachment
    xrayBrainrotBeam.Color = ColorSequence.new(currentThemeColor)
    xrayBrainrotBeam.Width0 = 0.35
    xrayBrainrotBeam.Width1 = 0.35
    xrayBrainrotBeam.FaceCamera = true
    xrayBrainrotBeam.Transparency = NumberSequence.new(0.05)
    xrayBrainrotBeam.Parent = root


    xrayNicknameBeam = Instance.new("Beam")
    xrayNicknameBeam.Name
