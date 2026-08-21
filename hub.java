local Players = game:GetService("Players")
local RunService = game:GetService("RunService")
local Stats = game:GetService("Stats")
local UserInputService = game:GetService("UserInputService")
local lp = Players.LocalPlayer
local playerGui = lp:WaitForChild("PlayerGui")

local STEAL_RADIUS = 60
local STEAL_DURATION = 1.4
local isStealing = false
local StealData = {}

local COLORS = {
	Border = Color3.fromRGB(58, 222, 126),
	MainBackground = Color3.fromRGB(15, 20, 18),
	TrackBackground = Color3.fromRGB(20, 31, 25),
	TextMint = Color3.fromRGB(153, 235, 192),
	TextWhite = Color3.fromRGB(240, 245, 245)
}

local FONTS = {
	Black = Enum.Font.GothamBlack,
	Bold = Enum.Font.GothamBold,
}

local screenGui = Instance.new("ScreenGui")
screenGui.Name = "AutoStealUI"
screenGui.ResetOnSpawn = false
screenGui.ZIndexBehavior = Enum.ZIndexBehavior.Sibling
screenGui.Parent = playerGui

-- Main draggable bar
local mainBar = Instance.new("Frame")
mainBar.Name = "MainBar"
mainBar.Size = UDim2.new(0, 320, 0, 36)
mainBar.Position = UDim2.new(0.5, -160, 0.5, -18)
mainBar.AnchorPoint = Vector2.new(0.5, 0.5)
mainBar.BackgroundColor3 = COLORS.MainBackground
mainBar.Parent = screenGui

local mainCorner = Instance.new("UICorner")
mainCorner.CornerRadius = UDim.new(0.5, 0)
mainCorner.Parent = mainBar

local mainStroke = Instance.new("UIStroke")
mainStroke.Color = COLORS.Border
mainStroke.Thickness = 2
mainStroke.Parent = mainBar

-- DRAG HANDLE (invisible but covers whole bar)
local dragHandle = Instance.new("Frame")
dragHandle.Name = "DragHandle"
dragHandle.Size = UDim2.new(1, 0, 1, 0)
dragHandle.BackgroundTransparency = 1
dragHandle.Parent = mainBar

-- Progress track
local progressTrack = Instance.new("Frame")
progressTrack.Name = "ProgressTrack"
progressTrack.Size = UDim2.new(0, 140, 0, 24)
progressTrack.Position = UDim2.new(0, 6, 0.5, 0)
progressTrack.AnchorPoint = Vector2.new(0, 0.5)
progressTrack.BackgroundColor3 = COLORS.TrackBackground
progressTrack.ClipsDescendants = true
progressTrack.Parent = mainBar

local trackCorner = Instance.new("UICorner")
trackCorner.CornerRadius = UDim.new(0.5, 0)
trackCorner.Parent = progressTrack

local progressFill = Instance.new("Frame")
progressFill.Name = "ProgressFill"
progressFill.Size = UDim2.new(0, 0, 1, 0)
progressFill.BackgroundColor3 = COLORS.Border
progressFill.Parent = progressTrack

local fillCorner = Instance.new("UICorner")
fillCorner.CornerRadius = UDim.new(0.5, 0)
fillCorner.Parent = progressFill

local stealText = Instance.new("TextLabel")
stealText.Name = "LabelSteal"
stealText.Size = UDim2.new(0, 50, 1, 0)
stealText.Position = UDim2.new(0, 8, 0, 0)
stealText.BackgroundTransparency = 1
stealText.Text = "STEAL"
stealText.Font = FONTS.Black
stealText.TextColor3 = COLORS.TextMint
stealText.TextSize = 11
stealText.TextXAlignment = Enum.TextXAlignment.Left
stealText.Parent = progressTrack

local percentText = Instance.new("TextLabel")
percentText.Name = "LabelPercent"
percentText.Size = UDim2.new(0, 30, 1, 0)
percentText.Position = UDim2.new(1, -6, 0, 0)
percentText.AnchorPoint = Vector2.new(1, 0)
percentText.BackgroundTransparency = 1
percentText.Text = "0%"
percentText.Font = FONTS.Black
percentText.TextColor3 = COLORS.Border
percentText.TextSize = 11
percentText.TextXAlignment = Enum.TextXAlignment.Right
percentText.Parent = progressTrack

local fpsText = Instance.new("TextLabel")
fpsText.Name = "LabelFPS"
fpsText.Size = UDim2.new(0, 50, 1, 0)
fpsText.Position = UDim2.new(0, 156, 0, 0)
fpsText.BackgroundTransparency = 1
fpsText.Text = "FPS:0"
fpsText.Font = FONTS.Bold
fpsText.TextColor3 = COLORS.TextWhite
fpsText.TextSize = 11
fpsText.TextXAlignment = Enum.TextXAlignment.Left
fpsText.Parent = mainBar

local pingEmoji = Instance.new("TextLabel")
pingEmoji.Name = "EmojiPing"
pingEmoji.Size = UDim2.new(0, 14, 0, 14)
pingEmoji.Position = UDim2.new(0, 216, 0.5, 0)
pingEmoji.AnchorPoint = Vector2.new(0, 0.5)
pingEmoji.BackgroundTransparency = 1
pingEmoji.Text = "📡"
pingEmoji.TextSize = 11
pingEmoji.Parent = mainBar

local pingText = Instance.new("TextLabel")
pingText.Name = "LabelPing"
pingText.Size = UDim2.new(0, 45, 1, 0)
pingText.Position = UDim2.new(0, 234, 0, 0)
pingText.BackgroundTransparency = 1
pingText.Text = "0ms"
pingText.Font = FONTS.Bold
pingText.TextColor3 = COLORS.TextWhite
pingText.TextSize = 11
pingText.TextXAlignment = Enum.TextXAlignment.Left
pingText.Parent = mainBar

local statusIndicator = Instance.new("Frame")
statusIndicator.Name = "StatusIndicator"
statusIndicator.Size = UDim2.new(0, 24, 0, 24)
statusIndicator.Position = UDim2.new(1, -6, 0.5, 0)
statusIndicator.AnchorPoint = Vector2.new(1, 0.5)
statusIndicator.BackgroundColor3 = COLORS.MainBackground
statusIndicator.Parent = mainBar

local statusCorner = Instance.new("UICorner")
statusCorner.CornerRadius = UDim.new(1, 0)
statusCorner.Parent = statusIndicator

local statusStroke = Instance.new("UIStroke")
statusStroke.Color = COLORS.Border
statusStroke.Thickness = 2
statusStroke.Parent = statusIndicator

local statusDot = Instance.new("Frame")
statusDot.Name = "StatusDot"
statusDot.Size = UDim2.new(0, 10, 0, 10)
statusDot.Position = UDim2.new(0.5, 0, 0.5, 0)
statusDot.AnchorPoint = Vector2.new(0.5, 0.5)
statusDot.BackgroundColor3 = COLORS.Border
statusDot.Parent = statusIndicator

local dotCorner = Instance.new("UICorner")
dotCorner.CornerRadius = UDim.new(1, 0)
dotCorner.Parent = statusDot

-- IMPROVED SMOOTH DRAGGING (Mouse & Touch Support)
local dragToggle = false
local dragStart, startPos

local function updateInput(input)
	local delta = input.Position - dragStart
	mainBar.Position = UDim2.new(
		startPos.X.Scale, 
		startPos.X.Offset + delta.X, 
		startPos.Y.Scale, 
		startPos.Y.Offset + delta.Y
	)
end

dragHandle.InputBegan:Connect(function(input)
	if input.UserInputType == Enum.UserInputType.MouseButton1 or input.UserInputType == Enum.UserInputType.Touch then
		dragToggle = true
		dragStart = input.Position
		startPos = mainBar.Position
		
		input.Changed:Connect(function()
			if input.UserInputState == Enum.UserInputState.End then
				dragToggle = false
			end
		end)
	end
end)

UserInputService.InputChanged:Connect(function(input)
	if dragToggle and (input.UserInputType == Enum.UserInputType.MouseMovement or input.UserInputType == Enum.UserInputType.Touch) then
		updateInput(input)
	end
end)


-- FUNCTIONS
local function updateTopBar()
    local fps = 60
    local ping = 0
    local framesCount = 0
    local last = tick()
    
    RunService.RenderStepped:Connect(function()
        framesCount = framesCount + 1
        if tick() - last >= 1 then
            fps = framesCount
            framesCount = 0
            last = tick()
        end
        
        local network = Stats:FindFirstChild("Network")
        if network and network:FindFirstChild("ServerStatsItem") then
            local dataPing = network.ServerStatsItem:FindFirstChild("Data Ping")
            if dataPing then 
                ping = math.floor(dataPing:GetValue()) 
            end
        end
        
        fpsText.Text = "FPS:" .. fps
        pingText.Text = ping .. "ms"
    end)
end

local function getHRP()
    local c = lp.Character
    if c then 
        return c:FindFirstChild("HumanoidRootPart") or c:FindFirstChild("Torso") or c:FindFirstChild("UpperTorso") 
    end
    return nil
end

local function isMyPlotByName(pn)
    local plots = workspace:FindFirstChild("Plots")
    if not plots then return false end
    local plot = plots:FindFirstChild(pn)
    if not plot then return false end
    local sign = plot:FindFirstChild("PlotSign")
    if sign then
        local yb = sign:FindFirstChild("YourBase")
        if yb and yb:IsA("BillboardGui") then return yb.Enabled == true end
    end
    return false
end

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

local function updateProgressBar(p)
    if progressFill then
        progressFill.Size = UDim2.new(p, 0, 1, 0)
    end
    if percentText then
        percentText.Text = math.floor(p * 100) .. "%"
    end
end

local function executeSteal(prompt)
    if isStealing then return end
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
    isStealing = true
    local startTime = tick()
    
    task.spawn(function()
        for _, f in ipairs(data.hold) do pcall(f) end
        while tick() - startTime < STEAL_DURATION do
            local elapsed = tick() - startTime
            local p = math.clamp(elapsed / STEAL_DURATION, 0, 1)
            updateProgressBar(p)
            task.wait()
        end
        updateProgressBar(1)
        
        for _, f in ipairs(data.trigger) do pcall(f) end
        task.wait(0.05)
        
        updateProgressBar(0)
        data.ready = true
        isStealing = false
    end)
end

local heartbeatConn
local function startAutoSteal()
    if heartbeatConn then return end
    updateTopBar()
    
    heartbeatConn = RunService.Heartbeat:Connect(function()
        if isStealing then return end
        local success, prompt = pcall(findNearestPrompt)
        if success and prompt then pcall(executeSteal, prompt) end
    end)
end

startAutoSteal()
