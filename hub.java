local Players = game:GetService("Players")
local RunService = game:GetService("RunService")
local Stats = game:GetService("Stats")
local UserInputService = game:GetService("UserInputService")
local lp = Players.LocalPlayer

local STEAL_RADIUS = 61
local STEAL_DURATION = 1.3
local StealData = {}

-- UI State
local uiState = {
    isStealing = false,
    isActive = false,
    heartbeatConn = nil
}

-- UI Elements container
local ui = {}

-- Helper: Update Progress Bar
local function updateProgressBar(p)
    if ui.ProgressFill then
        ui.ProgressFill.Size = UDim2.new(math.clamp(p, 0, 1), 0, 1, 0)
    end
    if ui.PercentLabel then
        ui.PercentLabel.Text = math.floor(p * 100) .. "%"
    end
end

-- Helper: Get HumanoidRootPart
local function getHRP()
    local c = lp.Character
    if c then
        return c:FindFirstChild("HumanoidRootPart") or c:FindFirstChild("Torso") or c:FindFirstChild("UpperTorso")
    end
    return nil
end

-- Helper: Check if plot belongs to player
local function isMyPlotByName(pn)
    local plots = workspace:FindFirstChild("Plots")
    if not plots then return false end
    local plot = plots:FindFirstChild(pn)
    if not plot then return false end
    local sign = plot:FindFirstChild("PlotSign")
    if sign then
        local yb = sign:FindFirstChild("YourBase")
        if yb and yb:IsA("BillboardGui") then
            return yb.Enabled == true
        end
    end
    return false
end

-- Helper: Find nearest Steal prompt
local function findNearestPrompt()
    local hrp = getHRP()
    if not hrp then return nil end
    local plots = workspace:FindFirstChild("Plots")
    if not plots then return nil end
    local nearest, dist = nil, math.huge
    for _, plot in ipairs(plots:GetChildren()) do
        if isMyPlotByName(plot.Name) then continue end
        local pods = plot:FindFirstChild("AnimalPodiums")
        if not pods then continue end
        for _, pod in ipairs(pods:GetChildren()) do
            local base = pod:FindFirstChild("Base")
            if not base then continue end
            local spawn = base:FindFirstChild("Spawn")
            if not spawn then continue end
            local d = (spawn.Position - hrp.Position).Magnitude
            if d <= STEAL_RADIUS and d < dist then
                local att = spawn:FindFirstChild("PromptAttachment")
                if att then
                    for _, p in ipairs(att:GetChildren()) do
                        if p:IsA("ProximityPrompt") and p.ActionText and p.ActionText:find("Steal") then
                            nearest, dist = p, d
                        end
                    end
                end
            end
        end
    end
    return nearest
end

-- Execute a single steal
local function executeSteal(prompt)
    if uiState.isStealing then return end
    if not StealData[prompt] then
        StealData[prompt] = {hold = {}, trigger = {}, ready = true}
        if getconnections then
            for _, c in ipairs(getconnections(prompt.PromptButtonHoldBegan)) do
                if c.Function then table.insert(StealData[prompt].hold, c.Function) end
            end
            for _, c in ipairs(getconnections(prompt.Triggered)) do
                if c.Function then table.insert(StealData[prompt].trigger, c.Function) end
            end
        end
    end
    local data = StealData[prompt]
    if not data.ready then return end
    data.ready = false
    uiState.isStealing = true
    local startTime = tick()
    task.spawn(function()
        for _, f in ipairs(data.hold) do pcall(f) end
        while tick() - startTime < STEAL_DURATION do
            local elapsed = tick() - startTime
            updateProgressBar(elapsed / STEAL_DURATION)
            task.wait()
        end
        updateProgressBar(1)
        for _, f in ipairs(data.trigger) do pcall(f) end
        task.wait(0.05)
        updateProgressBar(0)
        data.ready = true
        uiState.isStealing = false
    end)
end

-- Auto Steal Loop
local function startAutoSteal()
    if uiState.heartbeatConn then return end
    uiState.heartbeatConn = RunService.Heartbeat:Connect(function()
        if uiState.isStealing then return end
        local success, prompt = pcall(findNearestPrompt)
        if success and prompt then
            pcall(executeSteal, prompt)
        end
    end)
end

local function stopAutoSteal()
    if uiState.heartbeatConn then
        uiState.heartbeatConn:Disconnect()
        uiState.heartbeatConn = nil
    end
    uiState.isStealing = false
    updateProgressBar(0)
end

-- weekly is back
local function createUI()
    -- Clean old GUI
    local old1 = lp.PlayerGui:FindFirstChild("GalaxyUI")
    if old1 then old1:Destroy() end
    local old2 = lp.PlayerGui:FindFirstChild("J hub ")
    if old2 then old2:Destroy() end

    local sg = Instance.new("ScreenGui")
    sg.Name = "GalaxyUI"
    sg.ResetOnSpawn = false
    sg.Parent = lp.PlayerGui

    -- Main Frame (Smaller size: 210x55)
    local MainFrame = Instance.new("Frame")
    MainFrame.Size = UDim2.new(0, 210, 0, 55)
    MainFrame.Position = UDim2.new(0.5, 0, 0, 50)
    MainFrame.AnchorPoint = Vector2.new(0.5, 0)
    MainFrame.BackgroundColor3 = Color3.fromRGB(22, 8, 48)
    MainFrame.BorderSizePixel = 0
    MainFrame.Parent = sg
    Instance.new("UICorner", MainFrame).CornerRadius = UDim.new(0, 10)

    -- Top Bar (Draggable portion)
    local TopBar = Instance.new("Frame")
    TopBar.Size = UDim2.new(1, 0, 0, 28)
    TopBar.BackgroundTransparency = 1
    TopBar.Parent = MainFrame

    -- Title
    local Title = Instance.new("TextLabel")
    Title.Size = UDim2.new(0, 110, 1, 0)
    Title.Position = UDim2.new(0, 8, 0, 0)
    Title.BackgroundTransparency = 1
    Title.Font = Enum.Font.GothamBold
    Title.TextSize = 11
    Title.TextColor3 = Color3.fromRGB(255, 255, 255)
    Title.Text = "GALAXY AUTO STEAL"
    Title.TextXAlignment = Enum.TextXAlignment.Left
    Title.Parent = TopBar

    -- Toggle Button (Made smaller: 35x16)
    local ToggleBtn = Instance.new("TextButton")
    ToggleBtn.Size = UDim2.new(0, 35, 0, 16)
    ToggleBtn.Position = UDim2.new(1, -65, 0.5, -8)
    ToggleBtn.BackgroundColor3 = Color3.fromRGB(0, 200, 0)
    ToggleBtn.Text = "ON"
    ToggleBtn.TextColor3 = Color3.fromRGB(255, 255, 255)
    ToggleBtn.Font = Enum.Font.GothamBold
    ToggleBtn.TextSize = 10
    ToggleBtn.Parent = TopBar
    Instance.new("UICorner", ToggleBtn).CornerRadius = UDim.new(0, 6)

    -- Plus Button (Smaller: 18x16, permanently "+", does nothing when clicked)
    local PlusBtn = Instance.new("TextButton")
    PlusBtn.Size = UDim2.new(0, 18, 0, 16)
    PlusBtn.Position = UDim2.new(1, -30, 0.5, -8)
    PlusBtn.BackgroundColor3 = Color3.fromRGB(160, 80, 255)
    PlusBtn.Text = "+"
    PlusBtn.TextColor3 = Color3.fromRGB(255, 255, 255)
    PlusBtn.Font = Enum.Font.GothamBold
    PlusBtn.TextSize = 12
    PlusBtn.Parent = TopBar
    Instance.new("UICorner", PlusBtn).CornerRadius = UDim.new(0, 6)
    -- No function connected: pressing it literally does nothing.

    -- Bottom Area (Progress Bar + Radius)
    local BottomFrame = Instance.new("Frame")
    BottomFrame.Size = UDim2.new(1, 0, 0, 22)
    BottomFrame.Position = UDim2.new(0, 0, 0, 30)
    BottomFrame.BackgroundTransparency = 1
    BottomFrame.Parent = MainFrame

    -- Progress Bar Background
    local ProgressBg = Instance.new("Frame")
    ProgressBg.Size = UDim2.new(0, 130, 0, 10)
    ProgressBg.Position = UDim2.new(0, 8, 0.5, -5)
    ProgressBg.BackgroundColor3 = Color3.fromRGB(40, 40, 50)
    ProgressBg.Parent = BottomFrame
    Instance.new("UICorner", ProgressBg).CornerRadius = UDim.new(0, 5)

    -- Progress Bar Fill
    local ProgressFill = Instance.new("Frame")
    ProgressFill.Size = UDim2.new(0, 0, 1, 0)
    ProgressFill.BackgroundColor3 = Color3.fromRGB(160, 80, 255)
    ProgressFill.Parent = ProgressBg
    Instance.new("UICorner", ProgressFill).CornerRadius = UDim.new(0, 5)

    -- Percentage Label
    local PercentLabel = Instance.new("TextLabel")
    PercentLabel.Size = UDim2.new(1, 0, 1, 0)
    PercentLabel.BackgroundTransparency = 1
    PercentLabel.Font = Enum.Font.Gotham
    PercentLabel.TextSize = 9
    PercentLabel.TextColor3 = Color3.fromRGB(220, 220, 220)
    PercentLabel.Text = "0%"
    PercentLabel.Parent = ProgressBg

    -- Radius Label
    local RadiusLabel = Instance.new("TextLabel")
    RadiusLabel.Size = UDim2.new(0, 55, 0, 12)
    RadiusLabel.Position = UDim2.new(1, -60, 0.5, -6)
    RadiusLabel.BackgroundTransparency = 1
    RadiusLabel.Font = Enum.Font.Gotham
    RadiusLabel.TextSize = 10
    RadiusLabel.TextColor3 = Color3.fromRGB(150, 150, 150)
    RadiusLabel.Text = "Radius: 61"
    RadiusLabel.TextXAlignment = Enum.TextXAlignment.Right
    RadiusLabel.Parent = BottomFrame

    -- Store UI references
    ui.ProgressFill = ProgressFill
    ui.PercentLabel = PercentLabel

    -- Toggle ON/OFF logic
    ToggleBtn.MouseButton1Click:Connect(function()
        uiState.isActive = not uiState.isActive
        if uiState.isActive then
            ToggleBtn.Text = "ON"
            ToggleBtn.BackgroundColor3 = Color3.fromRGB(0, 200, 0)
            startAutoSteal()
        else
            ToggleBtn.Text = "OFF"
            ToggleBtn.BackgroundColor3 = Color3.fromRGB(200, 50, 50)
            stopAutoSteal()
        end
    end)

    -- Progress Bar Click => Manual Steal Trigger
    ProgressBg.InputBegan:Connect(function(input, gameProcessed)
        if gameProcessed then return end
        if input.UserInputType == Enum.UserInputType.MouseButton1 then
            if not uiState.isStealing and uiState.isActive then
                local prompt = findNearestPrompt()
                if prompt then
                    executeSteal(prompt)
                end
            end
        end
    end)

    -- -------------------------------------------------------------
    -- DRAGGABLE UI LOGIC (TopBar)
    -- -------------------------------------------------------------
    local dragging = false
    local dragInput = nil
    local dragStart = nil
    local startPos = nil

    TopBar.InputBegan:Connect(function(input)
        if input.UserInputType == Enum.UserInputType.MouseButton1 or input.UserInputType == Enum.UserInputType.Touch then
            dragging = true
            dragStart = input.Position
            startPos = MainFrame.Position
            
            input.Changed:Connect(function()
                if input.UserInputState == Enum.UserInputState.End then
                    dragging = false
                end
            end)
        end
    end)

    TopBar.InputChanged:Connect(function(input)
        if input.UserInputType == Enum.UserInputType.MouseMovement or input.UserInputType == Enum.UserInputType.Touch then
            dragInput = input
        end
    end)

    UserInputService.InputChanged:Connect(function(input)
        if input == dragInput and dragging then
            local delta = input.Position - dragStart
            MainFrame.Position = UDim2.new(
                startPos.X.Scale,
                startPos.X.Offset + delta.X,
                startPos.Y.Scale,
                startPos.Y.Offset + delta.Y
            )
        end
    end)
end

-- Wait for player and launch UI
local function waitForPlayer()
    while not lp or not lp.PlayerGui do
        task.wait()
    end
end
waitForPlayer()
createUI()
