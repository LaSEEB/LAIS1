% Script to process information from LAIS batch runs.
% Reads CSV files containing results and produces nice MATLAB graphics.
function laisplotbatch(folder, type, xlen)

% Parse second argument
if nargin == 1
    type = 'simple';
end;

% Get file list
files = dir(strcat(folder,'/*.csv'));
files = {files.name};

for i = 1:max(size(files))
    
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % Extract relevant information from file %
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    
    % Construct filename
    file = strcat('/',files(i));
    file = strcat(folder,file);
    % Get CSV file info
    data = csvread(file{1},9,3);
    % Open file to extract rest of info
    fid = fopen(file{1});
    % Extract Y axis name
    if max(size(regexp(file{1},'[.]*diversity[.]*'))) > 0
        axisY = 'Number of substances';
    elseif max(size(regexp(file{1},'[.]*Number of agents[.]*'))) > 0
        axisY = 'Number of agents';
    else
        axisY = 'Concentration';
    end;
    % Extract title
    fig_title = fgetl(fid);
    % Ignore irrelevant information
    for j=0:6
        fgets(fid);
    end;
    % Extract legends
    labels = fgets(fid);
    labelMatrix = regexp(labels,'[^",\n\r]+','match');
    labelMatrix = labelMatrix(4:max(size(labelMatrix)));
    %close file
    fclose(fid);
    
    %%%%%%%%%%%%%%%
    % Create plot %
    %%%%%%%%%%%%%%%
    if strcmpi(type,'simple')
        % Create figure
        figure;
    elseif strcmpi(type,'comp')
        % Determine subplot
        subplot(3,3,i);
    else
        error('Second parameter must be ''simple'' or ''comp''');
    end;
    % Plot data %
    plt = plot(data);
    % Set figure title
    %title(fig_title,'FontWeight','bold');    
    % Set axis
    if nargin == 3
        efectxlen = xlen;
    else
        efectxlen = max(size(data));
    end;    
    axis([0 efectxlen 0 max(max(data))*1.10]);
    % Set horizontal axis label
    xlabel('Ticks');
    % Set vertical axis label
    ylabel(axisY);
    % Show legends
    legend(labelMatrix);
    % Show grid
    grid('on');
    % Set line width
    % set(plt,'LineWidth',2);
    
    % Export to PDF
    %%%%%%% print('-dpdf',[folder '/' fig_title '.pdf']);
end;