local Players = game:GetService("Players")
local TeleportService = game:GetService("TeleportService")
local HttpService = game:GetService("HttpService")
local LocalPlayer = Players.LocalPlayer
getgenv().TigySettings = {
    brainrot = "5M/s",
    autoJoining = false,
    selectedBrainrot = nil,
}
local TigysAutoJoinerUI = Instance.new("ScreenGui")
TigysAutoJoinerUI.Name = "TigysAutoJoinerUI"
TigysAutoJoinerUI.ResetOnSpawn = false
TigysAutoJoinerUI.Parent = LocalPlayer:WaitForChild("PlayerGui")
local Frame = Instance.new("Frame")
Frame.Size = UDim2.new(0, 250, 0, 450)
Frame.Position = UDim2.new(0.5, -125, 0.5, -225)
Frame.BackgroundColor3 = Color3.fromRGB(10, 10, 10)
Frame.Active = true
Frame.Draggable = true
Frame.Parent = TigysAutoJoinerUI
local UICorner = Instance.new("UICorner")
UICorner.CornerRadius = UDim.new(0, 10)
UICorner.Parent = Frame
local TextLabel = Instance.new("TextLabel")
TextLabel.Size = UDim2.new(1, 0, 0, 34)
TextLabel.BackgroundTransparency = 1
TextLabel.Text = "Tigy's Auto Joiner"
TextLabel.Font = Enum.Font.GothamBold
TextLabel.TextSize = 18
TextLabel.TextColor3 = Color3.fromRGB(0, 255, 120)
TextLabel.Parent = Frame
local TextLabel_2 = Instance.new("TextLabel")
TextLabel_2.Size = UDim2.new(1, -20, 0, 20)
TextLabel_2.Position = UDim2.new(0, 10, 0, 34)
TextLabel_2.BackgroundTransparency = 1
TextLabel_2.Text = "*PAID VERSION* | TikTok - @wydtigy"
TextLabel_2.Font = Enum.Font.Gotham
TextLabel_2.TextSize = 12
TextLabel_2.TextColor3 = Color3.fromRGB(180, 180, 180)
TextLabel_2.TextWrapped = true
TextLabel_2.Parent = Frame
local TextButton = Instance.new("TextButton")
TextButton.Size = UDim2.new(1, -20, 0, 40)
TextButton.Position = UDim2.new(0, 10, 0, 60)
TextButton.Text = "Auto Joiner (OFF)"
TextButton.Font = Enum.Font.GothamBold
TextButton.TextSize = 16
TextButton.TextColor3 = Color3.fromRGB(255, 255, 255)
TextButton.BackgroundColor3 = Color3.fromRGB(20, 20, 20)
TextButton.BorderSizePixel = 0
TextButton.Parent = Frame
Instance.new("UICorner", TextButton).CornerRadius = UDim.new(0, 8)
local TextButton_2 = Instance.new("TextButton")
TextButton_2.Size = UDim2.new(1, -20, 0, 40)
TextButton_2.Position = UDim2.new(0, 10, 0, 110)
TextButton_2.Text = "Player ESP (OFF)"
TextButton_2.Font = Enum.Font.GothamBold
TextButton_2.TextSize = 16
TextButton_2.TextColor3 = Color3.fromRGB(255, 255, 255)
TextButton_2.BackgroundColor3 = Color3.fromRGB(20, 20, 20)
TextButton_2.BorderSizePixel = 0
TextButton_2.Parent = Frame
Instance.new("UICorner", TextButton_2).CornerRadius = UDim.new(0, 8)
local TextButton_3 = Instance.new("TextButton")
TextButton_3.Size = UDim2.new(1, -20, 0, 40)
TextButton_3.Position = UDim2.new(0, 10, 0, 160)
TextButton_3.Text = "Secret ESP (OFF)"
TextButton_3.Font = Enum.Font.GothamBold
TextButton_3.TextSize = 16
TextButton_3.TextColor3 = Color3.fromRGB(255, 255, 255)
TextButton_3.BackgroundColor3 = Color3.fromRGB(20, 20, 20)
TextButton_3.BorderSizePixel = 0
TextButton_3.Parent = Frame
Instance.new("UICorner", TextButton_3).CornerRadius = UDim.new(0, 8)
local TextLabel_3 = Instance.new("TextLabel")
TextLabel_3.Size = UDim2.new(1, -20, 0, 28)
TextLabel_3.Position = UDim2.new(0, 10, 1, -38)
TextLabel_3.BackgroundTransparency = 1
TextLabel_3.TextColor3 = Color3.fromRGB(200, 200, 200)
TextLabel_3.Font = Enum.Font.Gotham
TextLabel_3.TextSize = 13
TextLabel_3.Text = "Secrets: None"
TextLabel_3.TextWrapped = true
TextLabel_3.Parent = Frame
local Frame_2 = Instance.new("Frame")
Frame_2.Size = UDim2.new(1, -20, 0, 30)
Frame_2.Position = UDim2.new(0, 10, 0, 210)
Frame_2.BackgroundColor3 = Color3.fromRGB(20, 20, 20)
Frame_2.Parent = Frame
Instance.new("UICorner", Frame_2).CornerRadius = UDim.new(0, 8)
local TextButton_4 = Instance.new("TextButton")
TextButton_4.Size = UDim2.new(1, -10, 1, 0)
TextButton_4.Position = UDim2.new(0, 5, 0, 0)
TextButton_4.BackgroundTransparency = 1
TextButton_4.Text = "Brainrot: 5M/s ▼"
TextButton_4.Font = Enum.Font.GothamBold
TextButton_4.TextSize = 16
TextButton_4.TextColor3 = Color3.fromRGB(255, 255, 255)
TextButton_4.TextXAlignment = Enum.TextXAlignment.Left
TextButton_4.Parent = Frame_2
local Frame_3 = Instance.new("Frame")
Frame_3.Size = UDim2.new(1, -20, 0, 130)
Frame_3.Position = UDim2.new(0, 10, 0, 245)  -- Visible under the button
Frame_3.BackgroundColor3 = Color3.fromRGB(15, 15, 15)
Frame_3.Visible = false
Frame_3.Parent = Frame
Instance.new("UICorner", Frame_3).CornerRadius = UDim.new(0, 8)
local UIListLayout = Instance.new("UIListLayout")
UIListLayout.Padding = UDim.new(0, 5)
UIListLayout.FillDirection = Enum.FillDirection.Vertical
UIListLayout.Parent = Frame_3
local brainrots = {"50M/s", "100M/s", "15M/s", "10M/s", "5M/s"}
for _, name in ipairs(brainrots) do
    local btn = Instance.new("TextButton")
    btn.Size = UDim2.new(1, -10, 0, 30)
    btn.BackgroundColor3 = Color3.fromRGB(25, 25, 25)
    btn.Text = name
    btn.Font = Enum.Font.GothamBold
    btn.TextSize = 14
    btn.TextColor3 = Color3.fromRGB(255, 255, 255)
    btn.Parent = Frame_3
    Instance.new("UICorner", btn).CornerRadius = UDim.new(0, 6)
    btn.MouseButton1Click:Connect(function()
        TextButton_4.Text = "Brainrot: " .. name .. " ▼"
        Frame_3.Visible = false
    end)
end
TextButton_4.MouseButton1Click:Connect(function()
    Frame_3.Visible = not Frame_3.Visible
end)
local TextLabel_4 = Instance.new("TextLabel")
TextLabel_4.Size = UDim2.new(1, -20, 0, 20)
TextLabel_4.Position = UDim2.new(0, 10, 0, 380)
TextLabel_4.BackgroundTransparency = 1
TextLabel_4.TextColor3 = Color3.fromRGB(0, 255, 120)
TextLabel_4.Font = Enum.Font.GothamBold
TextLabel_4.TextSize = 14
TextLabel_4.Text = "Extras"
TextLabel_4.Parent = Frame
local Frame_4 = Instance.new("Frame")
Frame_4.Size = UDim2.new(1, -20, 0, 30)
Frame_4.Position = UDim2.new(0, 10, 0, 405)
Frame_4.BackgroundColor3 = Color3.fromRGB(20, 20, 20)
Frame_4.Parent = Frame
Instance.new("UICorner", Frame_4).CornerRadius = UDim.new(0, 8)
local TextButton_10 = Instance.new("TextButton")
TextButton_10.Size = UDim2.new(1, -10, 1, 0)
TextButton_10.BackgroundTransparency = 1
TextButton_10.Text = "Listed Brainrot: (None) ▼"
TextButton_10.Font = Enum.Font.GothamBold
TextButton_10.TextSize = 16
TextButton_10.TextColor3 = Color3.fromRGB(255, 255, 255)
TextButton_10.TextXAlignment = Enum.TextXAlignment.Left
TextButton_10.Parent = Frame_4
local Frame_5 = Instance.new("Frame")
Frame_5.Size = UDim2.new(1, -20, 0, 180)
Frame_5.Position = UDim2.new(0, 10, 0, 440)  -- Visible
Frame_5.BackgroundColor3 = Color3.fromRGB(15, 15, 15)
Frame_5.Visible = false
Frame_5.Parent = Frame
Instance.new("UICorner", Frame_5).CornerRadius = UDim.new(0, 8)
local UIListLayout_2 = Instance.new("UIListLayout")
UIListLayout_2.Padding = UDim.new(0, 5)
UIListLayout_2.FillDirection = Enum.FillDirection.Vertical
UIListLayout_2.Parent = Frame_5
local secretsList = {
    "La Vacca Saturno Saturnita", "Los Tralaleritos", "Las Tralaleritas", "Graipuss Medussi",
    "La Grande Combinasion", "Nuclearo Dinossauro", "Garama and Madundung", "Tortuginni Dragonfruitini",
    "Pot Hotspot", "Las Vaquitas Saturnitas", "Chicleteira Bicicleteira", "Agarrini la Palini",
    -- Add the rest from your original list if you want
    "La Cucaracha"  -- example
}
for _, secretName in ipairs(secretsList) do
    local btn = Instance.new("TextButton")
    btn.Size = UDim2.new(1, -10, 0, 30)
    btn.BackgroundColor3 = Color3.fromRGB(25, 25, 25)
    btn.Text = secretName
    btn.Font = Enum.Font.GothamBold
    btn.TextSize = 14
    btn.TextColor3 = Color3.fromRGB(255, 255, 255)
    btn.Parent = Frame_5
    Instance.new("UICorner", btn).CornerRadius = UDim.new(0, 6)
    btn.MouseButton1Click:Connect(function()
        TextButton_10.Text = "Listed Brainrot: " .. secretName .. " ▼"
        getgenv().TigySettings.selectedBrainrot = secretName
        Frame_5.Visible = false
    end)
end
TextButton_10.MouseButton1Click:Connect(function()
    Frame_5.Visible = not Frame_5.Visible
end)
local TextButton_43 = Instance.new("TextButton")
TextButton_43.Size = UDim2.new(1, -20, 0, 40)
TextButton_43.Position = UDim2.new(0, 10, 0, 630)  -- Adjusted for new layout
TextButton_43.Text = "Auto Join For Listed Brainrot (OFF)"
TextButton_43.Font = Enum.Font.GothamBold
TextButton_43.TextSize = 16
TextButton_43.TextColor3 = Color3.fromRGB(255, 255, 255)
TextButton_43.BackgroundColor3 = Color3.fromRGB(20, 20, 20)
TextButton_43.BorderSizePixel = 0
TextButton_43.Parent = Frame
Instance.new("UICorner", TextButton_43).CornerRadius = UDim.new(0, 8)
local playerESPEnabled = false
local secretESPEnabled = false
local autoJoinEnabled = false
local autoListedEnabled = false
local function createHighlight(part, color)
    local highlight = Instance.new("Highlight")
    highlight.FillColor = color
    highlight.OutlineColor = color
    highlight.FillTransparency = 0.5
    highlight.OutlineTransparency = 0
    highlight.Parent = part
end
local function checkForSecret()
    if not getgenv().TigySettings.selectedBrainrot then return false end
    for _, obj in ipairs(workspace:GetDescendants()) do
        if obj:IsA("Model") and obj.Name == getgenv().TigySettings.selectedBrainrot then
            return obj
        end
    end
    return false
end
TextButton_2.MouseButton1Click:Connect(function()
    playerESPEnabled = not playerESPEnabled
    TextButton_2.Text = "Player ESP (" .. (playerESPEnabled and "ON" or "OFF") .. ")"
    for _, plr in ipairs(Players:GetPlayers()) do
        if plr ~= LocalPlayer and plr.Character then
            if playerESPEnabled then
                createHighlight(plr.Character, Color3.fromRGB(0, 100, 255))
            else
                if plr.Character:FindFirstChildOfClass("Highlight") then
                    plr.Character:FindFirstChildOfClass("Highlight"):Destroy()
                end
            end
        end
    end
end)
TextButton_3.MouseButton1Click:Connect(function()
    secretESPEnabled = not secretESPEnabled
    TextButton_3.Text = "Secret ESP (" .. (secretESPEnabled and "ON" or "OFF") .. ")"
    if secretESPEnabled then
        spawn(function()
            while secretESPEnabled do
                local secret = checkForSecret()
                if secret then
                    createHighlight(secret, Color3.fromRGB(0, 255, 0))
                    TextLabel_3.Text = "Secrets: " .. getgenv().TigySettings.selectedBrainrot .. " FOUND!"
                else
                    TextLabel_3.Text = "Secrets: None"
                end
                task.wait(1)
            end
        end)
    end
end)
TextButton.MouseButton1Click:Connect(function()
    autoJoinEnabled = not autoJoinEnabled
    TextButton.Text = "Auto Joiner (" .. (autoJoinEnabled and "ON" or "OFF") .. ")"
    if autoJoinEnabled then
        spawn(function()
            while autoJoinEnabled do
                TeleportService:Teleport(game.PlaceId, LocalPlayer)
                task.wait(5)  -- Avoid rate limits
            end
        end)
    end
end)
TextButton_43.MouseButton1Click:Connect(function()
    autoListedEnabled = not autoListedEnabled
    TextButton_43.Text = "Auto Join For Listed Brainrot (" .. (autoListedEnabled and "ON" or "OFF") .. ")"
    if autoListedEnabled then
        if not getgenv().TigySettings.selectedBrainrot then
            TextButton_43.Text = "Select a Brainrot first!"
            autoListedEnabled = false
            return
        end
        spawn(function()
            while autoListedEnabled do
                local secret = checkForSecret()
                if secret then
                    TextLabel_3.Text = "Secrets: " .. getgenv().TigySettings.selectedBrainrot .. " FOUND! Stopping..."
                    autoListedEnabled = false
                    TextButton_43.Text = "Auto Join For Listed Brainrot (OFF)"
                    break
                end
                TeleportService:Teleport(game.PlaceId, LocalPlayer)
                task.wait(6)
            end
        end)
    end
end)
TextButton_4.Text = "Brainrot: 5M/s ▼"
waitForPlayer()
createUI()
